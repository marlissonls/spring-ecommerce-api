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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    @Transactional
    public AuthenticationResponseDto signIn(AccountCredentialsDto credentials) {
        logger.debug("Attempting to authenticate auth: '{}'", credentials.email());

        AuthenticatedUser auth =
                authenticationFacade.authenticate(credentials);

        AuthenticationResponseDto authResponse = getAuthenticationResponse(auth, "login");

        logger.info("User '{}' successfully authenticated.", auth.getEmail());

        return authResponse;
    }

    @Transactional
    public AuthenticationResponseDto refreshToken(RefreshTokenDto refresh) {
        logger.debug("Processing token refresh request.");

        AuthenticatedUser auth =
                tokenProvider.validateRefreshToken(refresh.refreshToken());

        AuthenticationResponseDto authResponse = getAuthenticationResponse(auth, "refresh");

        logger.info("Access token refreshed successfully.");

        return authResponse;
    }

    private @NonNull AuthenticationResponseDto getAuthenticationResponse(AuthenticatedUser auth, String requestType) {
        UUID customerId;
        String customerName;

        if (requestType.equals("login")) {
            Customer customer = customerService.findCustomer(auth.getUserId());
            customerId = customer.getId();
            customerName = customer.getName();
        } else {
            customerId = auth.getCustomerId();
            customerName = auth.getName();
        }

        TokenDto token = tokenProvider.issueTokens(
                auth.getUserId(),
                customerId,
                customerName,
                auth.getEmail(),
                auth.getRoles()
        );

        return new AuthenticationResponseDto(
                auth.getUserId(),
                customerId,
                customerName,
                auth.getEmail(),
                auth.getRoles(),
                token.accessToken(),
                token.refreshToken()
        );
    }
}



