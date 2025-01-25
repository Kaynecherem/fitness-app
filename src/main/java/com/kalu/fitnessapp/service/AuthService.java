package com.kalu.fitnessapp.service;

import com.kalu.fitnessapp.AppCustomException;
import com.kalu.fitnessapp.config.jwt.JwtProvider;
import com.kalu.fitnessapp.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    //API USERS AUTH
    public Map<String, String> authenticateUser(User user, HttpServletRequest httpServletRequest) {
        return userService.findByUsername(user.getUsername())
                .map(userFound -> {
                    if (passwordEncoder.matches(user.getPassword(), userFound.getPassword())) {
                        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                user.getUsername(), user.getPassword()
                        ));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        String jwt = jwtProvider.createJwt(auth, httpServletRequest);

                        return Map.of(
                                "token", jwt,
                                "token_type", "Bearer"
                        );
                    }

                    throw new AppCustomException("Bad Credentials");
                })
                .orElseThrow(() -> new AppCustomException("Bad Credentials"));
    }
}
