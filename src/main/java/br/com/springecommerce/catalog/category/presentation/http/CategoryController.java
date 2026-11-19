package br.com.springecommerce.catalog.category.presentation.http;

import br.com.springecommerce.catalog.category.application.dto.CategoryPublicDto;
import br.com.springecommerce.catalog.category.application.dto.CategoryRequestDto;
import br.com.springecommerce.catalog.category.application.dto.CategoryResponseDto;
import br.com.springecommerce.catalog.category.application.service.CategoryService;
import br.com.springecommerce.catalog.category.presentation.docs.CategoryControllerDocs;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController implements CategoryControllerDocs {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    // PUBLIC ENDPOINTS
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoryPublicDto>> findPublicCategories() {
        return ResponseEntity.ok().body(service.findPublicCategories());
    }

    @Override
    @GetMapping(path = "/slug/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryPublicDto> findBySlug(@PathVariable("slug") String slug) {
        return ResponseEntity.ok().body(service.findBySlug(slug));
    }

    // MANAGER ENDPOINTS
    @Override
    @GetMapping(path = "/manager", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<CategoryResponseDto>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @Override
    @GetMapping(path = "/manager/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryResponseDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @Override
    @PostMapping(path = "/manager", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryResponseDto> create(@RequestBody CategoryRequestDto dto) {
        CategoryResponseDto response = service.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Override
    @PutMapping(path = "/manager/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryResponseDto> update(
            @PathVariable("id") UUID id,
            @RequestBody CategoryRequestDto dto) {
        return ResponseEntity.ok().body(service.update(id, dto));
    }

    @Override
    @PatchMapping(path = "/manager/{id}/deactivate")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deactivate(@PathVariable("id") UUID id) {

        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping(path = "/manager/{id}/activate")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> activate(@PathVariable("id") UUID id) {

        service.activate(id);
        return ResponseEntity.noContent().build();
    }
}
