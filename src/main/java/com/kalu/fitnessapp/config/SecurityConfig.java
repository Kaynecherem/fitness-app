package com.kalu.fitnessapp.config;

import com.kalu.fitnessapp.config.jwt.JwtFilterChain;
import com.kalu.fitnessapp.config.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    void configureAuthenticationManager(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
            AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

        authenticationManagerBuilder                    //Comes from spring by default
                .userDetailsService(userDetailsService) //Instruct sprint to use our user details service
                .passwordEncoder(passwordEncoder);    //Instruct spring to use the BCrypt pass encoder we defined
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity, JwtProvider jwtProvider, UserDetailsService userDetailsService) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .sessionManagement(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(Customizer.withDefaults())
                .authorizeHttpRequests(customizer -> customizer.requestMatchers("/api/users/register", "/api/users/auth", "/error")
                        .permitAll().anyRequest().authenticated())
                .addFilterBefore(new JwtFilterChain(jwtProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
