package com.kalu.fitnessapp.service;

import com.kalu.fitnessapp.AppCustomException;
import com.kalu.fitnessapp.UserDeletedEvent;
import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public User registerUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new AppCustomException("User already exist with username specified");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); //Encode and store pass
        return userRepository.save(user);
    }

    // Method to find a user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Update user details
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // Delete a user by ID - this will also initiate an event to delete the user's goal and wellbeing logs in a single transaction
    @Transactional //This is an atomic operation
    public String deleteUser(User userToDelete) {
        eventPublisher.publishEvent(new UserDeletedEvent(userToDelete)); //Goals will be deleted, WellBeing will be deleted
        userRepository.delete(userToDelete); //After the deletion of Goals and WellbeingLogs, then user will be deleted
        return "User is removed: " + userToDelete.getId();
    }

    //Load user by username for authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Map roles to GrantedAuthority
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name())).toList();

        // Return UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    public Page<User> fetchAllUsers(Pageable page) {
        return userRepository.findAll(page);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

}
