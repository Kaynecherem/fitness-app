package com.kalu.fitnessapp.service;

import com.kalu.fitnessapp.AppCustomException;
import com.kalu.fitnessapp.UserDeletedEvent;
import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.entity.UserRole;
import com.kalu.fitnessapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        // Create a sample user to be used in tests
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testUser");
        sampleUser.setPassword("password");
        // Initially set roles empty; modify as needed in tests.
        sampleUser.setRoles(Collections.emptySet());
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User registeredUser = userService.registerUser(sampleUser);

        // Assert
        assertNotNull(registeredUser, "Registered user should not be null");
        assertEquals("encodedPassword", registeredUser.getPassword(), "Password should be encoded");
        verify(userRepository, times(1)).existsByUsername("testUser");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        // Act & Assert
        AppCustomException exception = assertThrows(AppCustomException.class, () -> {
            userService.registerUser(sampleUser);
        }, "Expected AppCustomException to be thrown when user already exists");
        assertEquals("User already exist with username specified", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("testUser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindByUsername() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(sampleUser));

        // Act
        Optional<User> userOpt = userService.findByUsername("testUser");

        // Assert
        assertTrue(userOpt.isPresent(), "User should be present");
        assertEquals("testUser", userOpt.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testUpdateUser() {
        // Arrange
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        // Act
        User updatedUser = userService.updateUser(sampleUser);

        // Assert
        assertNotNull(updatedUser, "Updated user should not be null");
        assertEquals("testUser", updatedUser.getUsername());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void testDeleteUser() {
        // Arrange
        // No return value for publishEvent or delete; just verify the calls.
        doNothing().when(eventPublisher).publishEvent(any(UserDeletedEvent.class));
        doNothing().when(userRepository).delete(sampleUser);

        // Act
        String result = userService.deleteUser(sampleUser);

        // Assert
        assertEquals("User is removed: " + sampleUser.getId(), result);
        verify(eventPublisher, times(1)).publishEvent(any(UserDeletedEvent.class));
        verify(userRepository, times(1)).delete(sampleUser);
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        // Set a role so the authorities are non-empty. Here we use a lambda for the role.
        sampleUser.setRoles(Collections.singleton(UserRole.STUDENT));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(sampleUser));

        // Act
        UserDetails userDetails = userService.loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertFalse(userDetails.getAuthorities().isEmpty(), "Authorities should not be empty");
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonExistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonExistent");
        }, "Expected UsernameNotFoundException for non-existent user");
        verify(userRepository, times(1)).findByUsername("nonExistent");
    }

    @Test
    void testFetchAllUsers() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        Page<User> page = mock(Page.class);
        when(userRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<User> resultPage = userService.fetchAllUsers(pageable);

        // Assert
        assertNotNull(resultPage, "Returned page should not be null");
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        // Act
        User user = userService.findUserById(1L);

        // Assert
        assertNotNull(user, "User should not be null");
        assertEquals(1L, user.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindUserById_NotFound() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findUserById(2L);
        }, "Expected RuntimeException when user is not found");
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(2L);
    }
}