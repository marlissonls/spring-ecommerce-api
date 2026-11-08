package br.com.springecommerce.iam.authentication.presentation.docs;

import br.com.springecommerce.iam.authentication.application.dto.AccountCredentialsDto;
import br.com.springecommerce.iam.authentication.application.dto.AuthenticationResponseDto;
import br.com.springecommerce.iam.authentication.application.dto.RefreshTokenDto;
import br.com.springecommerce.shared.exception.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication", description = "Endpoints for User Authentication")
public interface AuthControllerDocs {

    @Operation(
            summary = "Authenticates a user and returns a token",
            description = "Validates user credentials and generates an access token for role.",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<AuthenticationResponseDto> signIn(@Valid AccountCredentialsDto credentials);

    @Operation(
            summary = "Refresh token for authenticated user and returns a token",
            description = "Generates a new access token using the provided refresh token.",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<AuthenticationResponseDto> refresh(@Valid RefreshTokenDto refresh);
}
