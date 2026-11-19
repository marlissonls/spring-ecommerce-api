package br.com.springecommerce.iam.user.presentation.docs;

import br.com.springecommerce.iam.user.application.dto.UserCreateDto;
import br.com.springecommerce.iam.user.application.dto.UserEmployeeCreateDto;
import br.com.springecommerce.iam.user.application.dto.UserResponseDto;
import br.com.springecommerce.iam.user.application.dto.UserUpdateRoleDto;
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

@Tag(name = "Users", description = "Endpoints for Users management")
public interface UserControllerDocs {

    @Operation(
            summary = "Finding all Users",
            description = "Finds all Users with pagination",
            tags = {"Users"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<PageResponse<UserResponseDto>> findAll(@ParameterObject Pageable pageable);

    @Operation(
            summary = "Finding one User",
            description = "Finds a specific User based on the provided ID",
            tags = {"Users"},
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
    ResponseEntity<UserResponseDto> findById(UUID id);

    @Operation(
            summary = "Add a new User",
            description = "Adds a new User by passing a JSON representation of the User.",
            tags = {"Users"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<UserResponseDto> create(@Valid UserCreateDto dto);

    @Operation(
            summary = "Add a new User Employee",
            description = "Adds a new User Employee by passing a JSON representation of the User Employee.",
            tags = {"Users"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<UserResponseDto> createEmployee(@Valid UserEmployeeCreateDto dto);

    @Operation(
            summary = "Assign role to User",
            description = "Assigns a role to the specified User",
            tags = {"Users"},
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
    ResponseEntity<Void> assignRoleToUser(@Valid UserUpdateRoleDto dto);

    @Operation(
            summary = "Remove role from User",
            description = "Removes a role from the specified User",
            tags = {"Users"},
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
    ResponseEntity<Void> removeRoleFromUser(@Valid UserUpdateRoleDto dto);

    @Operation(
            summary = "Delete one User",
            description = "Deletes a User based on the provided ID",
            tags = {"Users"},
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

    @Operation(
            summary = "Deactivate user",
            description = "Deactivates a user account explicitly by userId. This action is intended for administrative control.",
            tags = {"Users"},
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
    ResponseEntity<Void> deactivate(UUID id);

    @Operation(
            summary = "Activate user",
            description = "Activates a user account explicitly by userId. This action is intended for administrative control.",
            tags = {"Users"},
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
    ResponseEntity<Void> activate(UUID id);
}
