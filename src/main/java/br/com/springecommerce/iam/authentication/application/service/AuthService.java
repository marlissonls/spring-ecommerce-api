package br.com.springecommerce.iam.authentication.application.service;

import br.com.springecommerce.config.security.jwt.JwtTokenProvider;
import br.com.springecommerce.customer.customer.application.service.CustomerService;
import br.com.springecommerce.customer.customer.domain.model.Customer;
import br.com.springecommerce.iam.authentication.application.dto.AccountCredentialsDto;
import br.com.springecommerce.iam.authentication.application.dto.AuthenticationResponseDto;
import br.com.springecommerce.iam.authentication.application.dto.RefreshTokenDto;
import br.com.springecommerce.iam.authentication.application.dto.TokenDto;
import br.com.springecommerce.iam.security.authentication.AuthenticationFacade;
import br.com.springecommerce.iam.security.principal.AuthenticatedUser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationFacade authenticationFacade;
    private final JwtTokenProvider tokenProvider;

    private final CustomerService customerService;

    public AuthService(
            AuthenticationFacade authenticationFacade,
            JwtTokenProvider tokenProvider,
            CustomerService customerService
    ) {
        this.authenticationFacade = authenticationFacade;
        this.tokenProvider = tokenProvider;
        this.customerService = customerService;
    }

    public AuthenticationResponseDto signIn(AccountCredentialsDto credentials) {
        logger.debug("Attempting to authenticate user: '{}'", credentials.email());

        AuthenticatedUser user =
                authenticationFacade.authenticate(credentials);

        AuthenticationResponseDto authResponse = getAuthenticationResponse(user);

        logger.info("User '{}' successfully authenticated.", user.getEmail());

        return authResponse;
    }

    public AuthenticationResponseDto refreshToken(RefreshTokenDto refresh) {
        logger.debug("Processing token refresh request.");

        AuthenticatedUser user =
                tokenProvider.validateRefreshToken(refresh.refreshToken());

        AuthenticationResponseDto authResponse = getAuthenticationResponse(user);

        logger.info("Access token refreshed successfully.");

        return authResponse;
    }

    private @NonNull AuthenticationResponseDto getAuthenticationResponse(AuthenticatedUser user) {
        Customer customer = customerService.findCustomer(user.getId());

        TokenDto token = tokenProvider.issueTokens(
                user.getId(),
                user.getEmail(),
                user.getRoles()
        );

        return new AuthenticationResponseDto(
                user.getId(),
                customer != null ? customer.getName() : null,
                user.getEmail(),
                user.getRoles(),
                token.accessToken(),
                token.refreshToken()
        );
    }
}



