package br.com.springecommerce.catalog.product.presentation.docs;

import br.com.springecommerce.catalog.product.application.dto.*;
import br.com.springecommerce.catalog.product.presentation.filter.ProductManagerFilter;
import br.com.springecommerce.catalog.product.presentation.filter.ProductPublicFilter;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Tag(name = "Products", description = "Endpoints for Products management")
public interface ProductControllerDocs {

    @Operation(
            summary = "Searching public Products",
            description = "Searches Products with public filters and pagination",
            tags = {"Products"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<PageResponse<ProductPublicResponseDto>> searchPublicViewProducts(
            @ParameterObject ProductPublicFilter filter,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Fetching Product by slug",
            description = "Finds a Product by its slug",
            tags = {"Products"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ProductPublicDetailsResponseDto> findBySlug(String slug);

    @Operation(
            summary = "Searching manager Products",
            description = "Searches Products with manager filters and pagination",
            tags = {"Products"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<PageResponse<ProductManagerResponseDto>> searchManagerViewProducts(
            @ParameterObject ProductManagerFilter filter,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Fetching Product by ID",
            description = "Finds a Product by its ID",
            tags = {"Products"},
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
    ResponseEntity<ProductManagerDetailsResponseDto> findById(UUID id);

    @Operation(
            summary = "Fetching Product by barcode",
            description = "Finds a Product by its barcode",
            tags = {"Products"},
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
    ResponseEntity<ProductManagerDetailsResponseDto> findByBarcode(String barcode);

    @Operation(
            summary = "Fetching Product by SKU",
            description = "Finds a Product by its SKU",
            tags = {"Products"},
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
    ResponseEntity<ProductManagerDetailsResponseDto> findBySku(String sku);

    @Operation(
            summary = "Creating a Product",
            description = "Creates a new Product from the provided JSON representation",
            tags = {"Products"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ProductResponseDto> create(@Valid ProductCreateDto dto);

    @Operation(
            summary = "Updating a Product",
            description = "Updates an existing Product from the provided JSON representation",
            tags = {"Products"},
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
    ResponseEntity<ProductResponseDto> update(
            UUID id,
            @Valid ProductUpdateDto dto
    );

    @Operation(
            summary = "Deactivating a Product",
            description = "Deactivates a Product by its ID",
            tags = {"Products"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<Void> deactivate(UUID id);

    @Operation(
            summary = "Activating a Product",
            description = "Activates a Product by its ID",
            tags = {"Products"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<Void> activate(UUID id);

    @Operation(
            summary = "Adding images to a Product",
            description = "Add images to a Product by its ID",
            tags = {"Products"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> addImages(
            UUID id,
            @Valid List<ProductImageCreateDto> images
    );
}

