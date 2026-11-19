package br.com.springecommerce.inventory.stock.presentation.docs;

import br.com.springecommerce.inventory.stock.application.dto.StockAvailableQuantityUpdateDto;
import br.com.springecommerce.inventory.stock.application.dto.StockCreateDto;
import br.com.springecommerce.inventory.stock.application.dto.StockMinimumQuantityUpdateDto;
import br.com.springecommerce.inventory.stock.application.dto.StockResponseDto;
import br.com.springecommerce.inventory.stock.presentation.filter.StockManagerFilter;
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


@Tag(name = "Stocks", description = "Endpoints for Stocks management")
@SecurityRequirement(name = "bearerAuth")
public interface StockControllerDocs {

    @Operation(
            summary = "Add a new Stock",
            description = "Creates a new stock record by providing its initial stock information.",
            tags = {"Stocks"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
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
    ResponseEntity<StockResponseDto> create(@Valid StockCreateDto dto);


    @Operation(
            summary = "Find all Stocks",
            description = "Retrieves all stock records with pagination and optional filtering. Results are sorted by available quantity by default.",
            tags = {"Stocks"},
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
    ResponseEntity<PageResponse<StockResponseDto>> findAll(
            @ParameterObject StockManagerFilter filter,
            @ParameterObject Pageable pageable
    );


    @Operation(
            summary = "Find one Stock",
            description = "Retrieves a specific stock record based on the provided stock ID.",
            tags = {"Stocks"},
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
    ResponseEntity<StockResponseDto> findById(UUID id);


    @Operation(
            summary = "Update minimum stock quantity",
            description = "Updates the minimum quantity threshold defined for the specified stock. This value can be used to identify when the available stock reaches a critical level.",
            tags = {"Stocks"},
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
    ResponseEntity<Void> updateMinimumQuantity(
            UUID id,
            @Valid StockMinimumQuantityUpdateDto dto
    );


    @Operation(
            summary = "Increase available stock",
            description = "Increases the available quantity of the specified stock by the amount provided.",
            tags = {"Stocks"},
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
    ResponseEntity<Void> increase(
            UUID id,
            @Valid StockAvailableQuantityUpdateDto dto
    );


    @Operation(
            summary = "Write off stock",
            description = "Reduces the available quantity of the specified stock by writing off the amount provided. This operation is intended for stock losses, damages, or other quantities that are no longer available.",
            tags = {"Stocks"},
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
    ResponseEntity<Void> writeOff(
            UUID id,
            @Valid StockAvailableQuantityUpdateDto dto
    );


    @Operation(
            summary = "Reserve stock",
            description = "Reserves the specified quantity of stock, reducing the quantity available for new reservations or other operations.",
            tags = {"Stocks"},
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
    ResponseEntity<Void> reserve(
            UUID id,
            @Valid StockAvailableQuantityUpdateDto dto
    );


    @Operation(
            summary = "Release reserved stock",
            description = "Releases the specified quantity of previously reserved stock, making it available again.",
            tags = {"Stocks"},
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
    ResponseEntity<Void> release(
            UUID id,
            @Valid StockAvailableQuantityUpdateDto dto
    );
}

