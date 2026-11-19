package br.com.springecommerce.customer.customer.presentation.docs;

import br.com.springecommerce.customer.customer.application.dto.CustomerCreateDto;
import br.com.springecommerce.customer.customer.application.dto.CustomerResponseDto;
import br.com.springecommerce.customer.customer.application.dto.CustomerUpdateDto;
import br.com.springecommerce.iam.security.principal.AuthenticatedUser;
import br.com.springecommerce.shared.exception.response.ExceptionResponse;
import br.com.springecommerce.shared.pagination.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Customers", description = "Endpoints for Customers management")
public interface CustomerControllerDocs {

    @Operation(
            summary = "Fetching all Customers",
            description = "Finds all Customers with pagination",
            tags = {"Customers"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<PageResponse<CustomerResponseDto>> findAll(
            // Flattens Pageable properties into individual query parameters (page, size, sort) in Swagger UI
            @ParameterObject Pageable pageable);

    @Operation(
            summary = "Fetching one Customer",
            description = "Finds a specific Customer based on the provided ID",
            tags = {"Customers"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<CustomerResponseDto> findById(UUID id);

    @Operation(
            summary = "Get current authenticated Customer",
            description = "Returns the profile data of the currently authenticated Customer",
            tags = {"Customers"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<CustomerResponseDto> me(AuthenticatedUser auth);

    @Operation(
            summary = "Add a new Customer",
            description = "Adds a new Customer by passing a JSON representation of the Customer.",
            tags = {"Customers"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<CustomerResponseDto> create(
            AuthenticatedUser auth,
            @Valid CustomerCreateDto dto
    );

    @Operation(
            summary = "Update a Customer information",
            description = "Updates a Customer's data by passing a JSON representation of the updated Customer and providing the Customer ID.",
            tags = {"Customers"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<CustomerResponseDto> update(
            UUID id,
            @Valid CustomerUpdateDto dto
    );

    @Operation(
            summary = "Delete one Customer",
            description = "Deletes a Customer based on the provided ID",
            tags = {"Customers"},
            responses = {
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<Void> delete(UUID id);
}
