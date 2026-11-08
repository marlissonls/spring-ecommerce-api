package br.com.springecommerce.customer.customer.presentation.http;

import br.com.springecommerce.customer.customer.application.dto.CustomerCreateDto;
import br.com.springecommerce.customer.customer.application.dto.CustomerResponseDto;
import br.com.springecommerce.customer.customer.application.dto.CustomerUpdateDto;
import br.com.springecommerce.customer.customer.application.service.CustomerService;
import br.com.springecommerce.customer.customer.presentation.docs.CustomerControllerDocs;
import br.com.springecommerce.iam.security.principal.AuthenticatedUser;
import br.com.springecommerce.shared.pagination.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController implements CustomerControllerDocs {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CustomerResponseDto>> findAll(
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(service.findAll(pageable));
    }

    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDto> findById(@PathVariable("id") UUID id) {

        return ResponseEntity.ok().body(service.findById(id));
    }

    @Override
    @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponseDto> me(@AuthenticationPrincipal AuthenticatedUser auth) {

        return ResponseEntity.ok().body(service.me(auth.getCustomerId()));
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponseDto> create(
            @AuthenticationPrincipal AuthenticatedUser auth,
            @RequestBody @Valid CustomerCreateDto dto
    ) {
        CustomerResponseDto response = service.create(auth.getCustomerId(), dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Override
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.getId().equals(#id)")
    public ResponseEntity<CustomerResponseDto> update(
            @PathVariable("id") UUID id,
            @RequestBody @Valid CustomerUpdateDto dto)
    {
        return ResponseEntity.ok().body(service.update(id, dto));
    }

    @Override
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.getId().equals(#id)")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}