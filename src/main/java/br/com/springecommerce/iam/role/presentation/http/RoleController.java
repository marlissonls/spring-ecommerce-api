package br.com.springecommerce.iam.role.presentation.http;

import br.com.springecommerce.iam.role.application.dto.RoleCreateDto;
import br.com.springecommerce.iam.role.application.dto.RoleResponseDto;
import br.com.springecommerce.iam.role.application.dto.RoleUpdateDto;
import br.com.springecommerce.iam.role.application.service.RoleService;
import br.com.springecommerce.iam.role.presentation.docs.RoleControllerDocs;
import br.com.springecommerce.shared.pagination.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController implements RoleControllerDocs {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<RoleResponseDto>> findAll(
            @PageableDefault(size = 12, sort = "title", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(service.findAll(pageable));
    }

    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleResponseDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @Override
    @GetMapping(value = "/title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleResponseDto> findByTitle(
            @PathVariable("title") String title
    ) {
        return ResponseEntity.ok(service.findByTitle(title));
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleResponseDto> create(
            @RequestBody RoleCreateDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    @Override
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleResponseDto> update(
            @PathVariable("id") UUID id,
            @RequestBody RoleUpdateDto dto
    ) {
        return ResponseEntity.ok().body(service.update(id, dto));
    }
}
