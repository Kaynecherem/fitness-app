package com.kalu.fitnessapp.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

//Verifies that callers of my endpoint has a JWT
//Set Authentication iff a JWT exists in the incoming request
@AllArgsConstructor
public class JwtFilterChain extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        getJwtFromHttpRequest(request)
                .ifPresent(jwt -> {
                    String username = jwtProvider.validateJwtAndReturnUsername(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                });

        filterChain.doFilter(request, response); //Hand over execution to the next filter in the chain
    }

    //Check either the header or the session's cookies for the jwt
    private Optional<String> getJwtFromHttpRequest(HttpServletRequest request) {

        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .map(bearerToken -> bearerToken.substring(7))
                .or(() -> Optional.ofNullable(request.getSession())
                        .flatMap(session -> Optional.ofNullable(session.getAttribute("jwt"))
                                .map(String::valueOf)));
    }
}
