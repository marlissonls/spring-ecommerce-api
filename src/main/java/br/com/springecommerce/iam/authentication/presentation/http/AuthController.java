package br.com.springecommerce.iam.authentication.presentation.http;

import br.com.springecommerce.iam.authentication.application.dto.AccountCredentialsDto;
import br.com.springecommerce.iam.authentication.application.dto.RefreshTokenDto;
import br.com.springecommerce.iam.authentication.application.dto.TokenDto;
import br.com.springecommerce.iam.authentication.application.service.AuthService;
import br.com.springecommerce.iam.authentication.presentation.docs.AuthControllerDocs;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Override
    @PostMapping(path = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenDto> signIn(@RequestBody AccountCredentialsDto credentials) {

        return ResponseEntity.ok().body(service.signIn(credentials));
    }

    @Override
    @PostMapping(path = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenDto> refresh(@RequestBody RefreshTokenDto refresh) {

        return ResponseEntity.ok().body(service.refreshToken(refresh));
    }
}
