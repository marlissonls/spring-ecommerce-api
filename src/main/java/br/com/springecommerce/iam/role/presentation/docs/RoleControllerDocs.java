package br.com.springecommerce.iam.role.presentation.docs;

import br.com.springecommerce.iam.role.application.dto.RoleCreateDto;
import br.com.springecommerce.iam.role.application.dto.RoleResponseDto;
import br.com.springecommerce.iam.role.application.dto.RoleUpdateDto;
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

@Tag(name = "Roles", description = "Endpoints for Roles management")
public interface RoleControllerDocs {

    @Operation(
            summary = "Finding all Roles",
            description = "Finds all Roles with pagination",
            tags = {"Roles"},
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
    ResponseEntity<PageResponse<RoleResponseDto>> findAll(@ParameterObject Pageable pageable);

    @Operation(
            summary = "Finding one Role by ID",
            description = "Finds a specific Role based on the provided ID",
            tags = {"Roles"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<RoleResponseDto> findById(UUID id);

    @Operation(
            summary = "Finding one Role by title",
            description = "Finds a specific Role based on the provided title",
            tags = {"Roles"},
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
    ResponseEntity<RoleResponseDto> findByTitle(String title);

    @Operation(
            summary = "Add a new Role",
            description = "Adds a new Role by passing a JSON representation of the Role.",
            tags = {"Roles"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<RoleResponseDto> create(@Valid RoleCreateDto dto);

    @Operation(
            summary = "Update one Role",
            description = "Updates a Role based on the provided ID and JSON representation of the Role.",
            tags = {"Roles"},
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
    ResponseEntity<RoleResponseDto> update(
            UUID id,
            @Valid RoleUpdateDto dto
    );
}