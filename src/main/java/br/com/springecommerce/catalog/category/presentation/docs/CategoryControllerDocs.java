package br.com.springecommerce.catalog.category.presentation.docs;

import br.com.springecommerce.catalog.category.application.dto.CategoryPublicDto;
import br.com.springecommerce.catalog.category.application.dto.CategoryRequestDto;
import br.com.springecommerce.catalog.category.application.dto.CategoryResponseDto;
import br.com.springecommerce.shared.exception.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Categories", description = "Endpoints for Categories management")
public interface CategoryControllerDocs {

    // PUBLIC ENDPOINTS

    @Operation(
            summary = "Find public categories",
            description = "Retrieves all active categories available for public access.",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
                    ),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<List<CategoryPublicDto>> findPublicCategories();


    @Operation(
            summary = "Find category by slug",
            description = "Retrieves a public category based on its unique slug.",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
                    ),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<CategoryPublicDto> findBySlug(String slug);

    // MANAGER ENDPOINTS

    @Operation(
            summary = "Find all categories",
            description = "Retrieves all categories for management purposes.",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<List<CategoryResponseDto>> findAll();

    @Operation(
            summary = "Find one category",
            description = "Retrieves a specific category based on the provided category ID.",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
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
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<CategoryResponseDto> findById(UUID id);

    @Operation(
            summary = "Add a new category",
            description = "Creates a new category by providing its name and slug information.",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<CategoryResponseDto> create(@Valid CategoryRequestDto dto);

    @Operation(
            summary = "Update a category",
            description = "Updates the information of an existing category based on the provided category ID.",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<CategoryResponseDto> update(
            UUID id,
            @Valid CategoryRequestDto dto
    );

    @Operation(
            summary = "Deactivate a category",
            description = "Deactivates an existing category based on the provided category ID, preventing it from being available through public category endpoints.",
            tags = {"Categories"},
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
                    @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<Void> deactivate(UUID id);

    @Operation(
            summary = "Activate a category",
            description = "Activates an existing category based on the provided category ID, making it available through public category endpoints.",
            tags = {"Categories"},
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
                    @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<Void> activate(UUID id);
}
