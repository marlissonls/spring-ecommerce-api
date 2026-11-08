package br.com.springecommerce.iam.user.presentation.http;

import br.com.springecommerce.iam.user.application.dto.UserCreateDto;
import br.com.springecommerce.iam.user.application.dto.UserEmployeeCreateDto;
import br.com.springecommerce.iam.user.application.dto.UserResponseDto;
import br.com.springecommerce.iam.user.application.dto.UserUpdateRoleDto;
import br.com.springecommerce.iam.user.application.service.UserCommandService;
import br.com.springecommerce.iam.user.application.service.UserQueryService;
import br.com.springecommerce.iam.user.presentation.docs.UserControllerDocs;
import br.com.springecommerce.shared.pagination.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {

    private final UserQueryService queryService;
    private final UserCommandService commandService;

    public UserController(
            UserQueryService queryService,
            UserCommandService commandService
    ) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponseDto>> findAll(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {

        return ResponseEntity.ok().body(queryService.findAll(pageable));
    }

    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> findById(@PathVariable("id") UUID id) {

        return ResponseEntity.ok().body(queryService.findById(id));
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid UserCreateDto dto) {

        UserResponseDto response = commandService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Override
    @PostMapping(path = "/employee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createEmployee(@RequestBody @Valid UserEmployeeCreateDto dto) {

        UserResponseDto response = commandService.createEmployee(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Override
    @PutMapping(path = "/assignRole", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignRoleToUser(@RequestBody @Valid UserUpdateRoleDto dto) {

        commandService.assignRoleToUser(dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping(path = "/removeRole", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeRoleFromUser(@RequestBody @Valid UserUpdateRoleDto dto) {

        commandService.removeRoleFromUser(dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.getId().equals(#id)")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {

        commandService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping(path = "/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable("id") UUID id) {

        commandService.deactivate(id);
        return ResponseEntity.noContent().build();
    }


    @Override
    @PatchMapping(path = "/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activate(@PathVariable("id") UUID id) {

        commandService.activate(id);
        return ResponseEntity.noContent().build();
    }
}