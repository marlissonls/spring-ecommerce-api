package br.com.springecommerce.sales.cart.presentation.docs;

import br.com.springecommerce.iam.security.principal.AuthenticatedUser;
import br.com.springecommerce.sales.cart.application.dto.CartAddProductRequestDto;
import br.com.springecommerce.sales.cart.application.dto.CartRemoveProductsRequestDto;
import br.com.springecommerce.sales.cart.application.dto.CartResponseDto;
import br.com.springecommerce.shared.exception.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Carts", description = "Endpoints for Carts management")
@SecurityRequirement(name = "bearerAuth")
public interface CartControllerDocs {

    @Operation(
            summary = "Find current user's cart",
            description = "Retrieves the shopping cart associated with the currently authenticated user.",
            tags = {"Carts"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "401",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
                    ),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<CartResponseDto> getCart(AuthenticatedUser auth);


    @Operation(
            summary = "Add product to cart",
            description = "Adds the specified product and quantity to the shopping cart of the currently authenticated user.",
            tags = {"Carts"},
            responses = {
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> addProduct(
            AuthenticatedUser auth,
            @Valid CartAddProductRequestDto dto
    );


    @Operation(
            summary = "Remove product from cart",
            description = "Removes the specified product from the shopping cart of the currently authenticated user.",
            tags = {"Carts"},
            responses = {
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> removeProducts(
            AuthenticatedUser auth,
            @Valid CartRemoveProductsRequestDto dto
    );
}

