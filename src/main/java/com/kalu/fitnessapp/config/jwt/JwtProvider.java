package com.kalu.fitnessapp.config.jwt;

import com.kalu.fitnessapp.AppCustomException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

//Will build a Jwt String when required
@Configuration
public class JwtProvider {

    private final JwtParser jwtParser;
    private final SecretKey signingKey;
    private final long tokenExpirationInMillis;

    public JwtProvider(@Value("${jjwt-secret-signer}") String secretKeyValue) {

        final byte[] keyBytes =  secretKeyValue.getBytes(StandardCharsets.UTF_8);
        final SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

        this.signingKey = secretKey;
        this.tokenExpirationInMillis = 7_2000_000L; //2hrs
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    public String createJwt(Authentication authentication, HttpServletRequest request) {

        final Date now = new Date();
        final String subject = authentication.getName();
        final Date expiration = new Date(now.getTime() + tokenExpirationInMillis);

        return Jwts.builder().issuedAt(now)
                .issuer(request.getRemoteHost())
                .signWith(signingKey)
                .expiration(expiration)
                .subject(subject)
                .compact();
    }

    public String validateJwtAndReturnUsername(String jwt) {
        try {
            var claims = jwtParser.parseSignedClaims(jwt);
            return claims.getPayload().getSubject();
        } catch (Exception e) {
            throw new AppCustomException(e.getMessage());
        }
    }
}
