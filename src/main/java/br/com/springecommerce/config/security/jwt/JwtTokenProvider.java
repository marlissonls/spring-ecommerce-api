package br.com.springecommerce.config.security.jwt;

import br.com.springecommerce.config.security.jwt.exception.InvalidJwtTokenException;
import br.com.springecommerce.iam.authentication.application.dto.TokenDto;
import br.com.springecommerce.iam.authentication.domain.exception.AuthErrors;
import br.com.springecommerce.iam.security.principal.AuthenticatedUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JwtTokenProvider {

    private final long validityInMilliseconds;
    private final long refreshValidityInMilliseconds;
    private final Algorithm algorithm;
    private final JWTVerifier accessTokenVerifier;
    private final JWTVerifier refreshTokenVerifier;
    private static final Pattern JWT_PATTERN =
            Pattern.compile("[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+");

    public JwtTokenProvider(
            String secret,
            Duration validity,
            Duration refreshValidity
    ) {
        this.validityInMilliseconds = validity.toMillis();
        this.refreshValidityInMilliseconds = refreshValidity.toMillis();
        this.algorithm = Algorithm.HMAC256(secret);
        this.accessTokenVerifier = JWT
                .require(algorithm)
                .withClaim("type", "access")
                .build();

        this.refreshTokenVerifier = JWT
                .require(algorithm)
                .withClaim("type", "refresh")
                .build();
    }

    public TokenDto createAccessToken(UUID userId, String email, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        Date refreshValidity = new Date(now.getTime() + refreshValidityInMilliseconds);
        String accessToken = getAccessToken(userId, email, roles, now, validity);
        String refreshToken = getRefreshToken(userId, email, roles, now, refreshValidity);

        return new TokenDto(userId, email, roles, true, now, validity, accessToken, refreshToken);
    }

    private String getAccessToken(UUID userId, String email, List<String> roles, Date now, Date validity) {

        String issuerUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .build()
                .toUriString();

        return JWT.create()
                .withSubject(email)
                .withClaim("id", userId.toString())
                .withClaim("roles", roles)
                .withClaim("type", "access")
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withIssuer(issuerUrl)
                .sign(algorithm);
    }

    private String getRefreshToken(UUID userId, String email, List<String> roles, Date now, Date refreshValidity) {

        String jti = UUID.randomUUID().toString();

        return JWT.create()
                .withSubject(email)
                .withClaim("id", userId.toString())
                .withClaim("roles", roles)
                .withClaim("jti", jti)
                .withClaim("type", "refresh")
                .withIssuedAt(now)
                .withExpiresAt(refreshValidity)
                .sign(algorithm);
    }



    public Optional<String> resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        return extractToken(bearerToken);
    }

    private static Optional<String> extractToken(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        Matcher matcher = JWT_PATTERN.matcher(value);

        return matcher.find()
            ? Optional.of(matcher.group())
            : Optional.empty();
    }

    public Authentication getAccessAuthentication(String token) {
        try {
            DecodedJWT decodedJWT = accessTokenVerifier.verify(token);

            UUID id = decodedJWT.getClaim("id").as(UUID.class);
            String email = decodedJWT.getSubject();

            var roles = decodedJWT.getClaim("roles").asList(String.class);
            var authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            AuthenticatedUser principal = new AuthenticatedUser(id, email, authorities);

            return new UsernamePasswordAuthenticationToken(principal, null, authorities);

        } catch (JWTVerificationException e) {
            throw new InvalidJwtTokenException(
                    AuthErrors.Target.ACCESS_TOKEN,
                    AuthErrors.Code.ACCESS_TOKEN_INVALID
            );
        }
    }

    public AuthenticatedUser validateRefreshToken(String bearerToken) {
        try {
            Optional<String> refreshToken = extractToken(bearerToken);

            if (refreshToken.isEmpty()) {
                throw new InvalidJwtTokenException(
                        AuthErrors.Target.REFRESH_TOKEN,
                        AuthErrors.Code.REFRESH_TOKEN_INVALID
                );
            }

            DecodedJWT decodedJWT = refreshTokenVerifier.verify(refreshToken.get());

            UUID id = decodedJWT.getClaim("id").as(UUID.class);
            String email = decodedJWT.getSubject();
            var roles = decodedJWT.getClaim("roles").asList(String.class);
            var authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return new AuthenticatedUser(id, email, authorities);
        } catch (JWTVerificationException e) {
            throw new InvalidJwtTokenException(
                    AuthErrors.Target.REFRESH_TOKEN,
                    AuthErrors.Code.REFRESH_TOKEN_INVALID
            );
        }
    }

    public TokenDto issueTokens(
            UUID id,
            String email,
            Collection<? extends GrantedAuthority> authorities
    ) {
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return createAccessToken(id, email, roles);
    }
}
