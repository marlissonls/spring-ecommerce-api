package br.com.springecommerce.catalog.brand.presentation.http;

import br.com.springecommerce.catalog.brand.application.dto.BrandPublicDto;
import br.com.springecommerce.catalog.brand.application.dto.BrandRequestDto;
import br.com.springecommerce.catalog.brand.application.dto.BrandResponseDto;
import br.com.springecommerce.catalog.brand.application.service.BrandService;
import br.com.springecommerce.catalog.brand.presentation.docs.BrandControllerDocs;
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
@RequestMapping("/api/v1/brands")
public class BrandController implements BrandControllerDocs {

    private final BrandService service;

    public BrandController(BrandService service) {
        this.service = service;
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BrandPublicDto>> findPublicBrands() {
        return ResponseEntity.ok().body(service.findPublicCategories());
    }

    @Override
    @GetMapping(path = "/slug/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BrandPublicDto> findBySlug(@PathVariable("slug") String slug) {
        return ResponseEntity.ok().body(service.findBySlug(slug));
    }

    @Override
    @GetMapping(path = "/manager", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<BrandResponseDto>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @Override
    @PostMapping(path = "/manager", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<BrandResponseDto> create(@RequestBody @Valid BrandRequestDto dto) {
        BrandResponseDto response = service.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Override
    @GetMapping(path = "/manager/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<BrandResponseDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @Override
    @PutMapping(path = "/manager/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<BrandResponseDto> update(
            @PathVariable("id") UUID id,
            @RequestBody @Valid BrandRequestDto dto) {
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
