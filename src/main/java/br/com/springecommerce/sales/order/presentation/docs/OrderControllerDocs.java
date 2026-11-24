package br.com.springecommerce.sales.order.presentation.docs;

import br.com.springecommerce.iam.security.principal.AuthenticatedUser;
import br.com.springecommerce.sales.order.application.dto.OrderResponseDto;
import br.com.springecommerce.sales.order.presentation.filter.OrderCustomerFilter;
import br.com.springecommerce.sales.order.presentation.filter.OrderManagerFilter;
import br.com.springecommerce.shared.exception.response.ExceptionResponse;
import br.com.springecommerce.shared.pagination.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Orders", description = "Endpoints for Orders management")
@SecurityRequirement(name = "bearerAuth")
public interface OrderControllerDocs {

    @Operation(
            summary = "Find one Order",
            description = "Retrieves a specific order belonging to the currently authenticated user based on the provided order ID.",
            tags = {"Orders"},
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
    ResponseEntity<OrderResponseDto> getOrder(
            AuthenticatedUser auth,
            UUID id
    );


    @Operation(
            summary = "Find all Orders",
            description = "Retrieves all orders belonging to the currently authenticated user with pagination, sorted by creation date in descending order by default.",
            tags = {"Orders"},
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
    ResponseEntity<PageResponse<OrderResponseDto>> getCustomerViewOrders(
            AuthenticatedUser auth,
            @ParameterObject OrderCustomerFilter filter,
            @ParameterObject Pageable pageable
    );


    @Operation(
            summary = "Find all Orders",
            description = "Retrieves all orders belonging to the currently authenticated user with pagination, sorted by creation date in descending order by default.",
            tags = {"Orders"},
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
    ResponseEntity<PageResponse<OrderResponseDto>> getManagerViewOrders(
            @ParameterObject OrderManagerFilter filter,
            @ParameterObject Pageable pageable
    );


    @Operation(
            summary = "Cancel an Order",
            description = "Cancels a specific order belonging to the currently authenticated user based on the provided order ID.",
            tags = {"Orders"},
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
    ResponseEntity<Void> cancelOrder(
            AuthenticatedUser auth,
            UUID id
    );
}
