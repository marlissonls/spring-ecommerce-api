package br.com.springecommerce.catalog.product.presentation.http;

import br.com.springecommerce.catalog.product.application.dto.*;
import br.com.springecommerce.catalog.product.application.service.ProductService;
import br.com.springecommerce.catalog.product.presentation.docs.ProductControllerDocs;
import br.com.springecommerce.catalog.product.presentation.filter.ProductManagerFilter;
import br.com.springecommerce.catalog.product.presentation.filter.ProductPublicFilter;
import br.com.springecommerce.shared.pagination.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController implements ProductControllerDocs {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // PUBLIC ENDPOINTS
    // searchPublicViewProducts
    // ?search={search}&category={category}&brand={brand}&onSale={onSale}&featured={featured}&isNew={isNew}&minPrice={minPrice}&maxPrice={maxPrice}
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<ProductPublicResponseDto>> searchPublicViewProducts(
            @ModelAttribute ProductPublicFilter filter,
            @PageableDefault(size = 12, sort = "title") Pageable pageable
    ) {
        return ResponseEntity.ok().body(
            service.getPublicViewProducts(filter, pageable)
        );
    }

    // findBySlug
    @Override
    @GetMapping(path = "/slug/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductPublicDetailsResponseDto> findBySlug(@PathVariable("slug") String slug) {
        return ResponseEntity.ok().body(service.findBySlug(slug));
    }

    // PRIVATE ENDPOINTS
    // searchManagerViewProducts
    @Override
    @GetMapping(path = "/manager", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PageResponse<ProductManagerResponseDto>> searchManagerViewProducts(
            @ModelAttribute ProductManagerFilter filter,
            @PageableDefault(size = 12, sort = "title") Pageable pageable
    ) {
        return ResponseEntity.ok().body(
            service.getManagerViewProducts(filter, pageable)
        );
    }

    // findById
    @Override
    @GetMapping(path = "/manager/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductManagerDetailsResponseDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    // findByBarcode
    @Override
    @GetMapping(path = "/manager/barcode/{barcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductManagerDetailsResponseDto> findByBarcode(@PathVariable("barcode") String barcode) {
        return ResponseEntity.ok().body(service.findByBarcode(barcode));
    }

    // findBySku
    @Override
    @GetMapping(path = "/manager/sku/{sku}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductManagerDetailsResponseDto> findBySku(@PathVariable("sku") String sku) {
        return ResponseEntity.ok().body(service.findBySku(sku));
    }

    // create
    @Override
    @PostMapping(path = "/manager", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductResponseDto> create(@RequestBody ProductCreateDto dto) {
        ProductResponseDto response = service.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // update
    @Override
    @PutMapping(path = "/manager/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable("id") UUID id,
            @RequestBody ProductUpdateDto dto) {
        return ResponseEntity.ok().body(service.update(id, dto));
    }

    // deactivate
    @Override
    @PatchMapping(path = "/manager/{id}/deactivate")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deactivate(@PathVariable("id") UUID id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // activate
    @Override
    @PatchMapping(path = "/manager/{id}/activate")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> activate(@PathVariable("id") UUID id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/manager/{id}/images", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> addImages(
            @PathVariable("id") UUID id,
            @RequestBody @Valid List<ProductImageCreateDto> images
    ) {
        service.addImages(id, images);
        return ResponseEntity.noContent().build();
    }

//    @PatchMapping("/manager/{id}/images")
//    @PreAuthorize("hasRole('MANAGER')")
//    public ResponseEntity<Void> updateImages(
//            @PathVariable("id") UUID id,
//            @RequestBody @Valid List<ProductImageCreateDto> images
//    ) {
//        service.updateImages(id, images);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/manager/{id}/images/{publicId}")
//    @PreAuthorize("hasRole('MANAGER')")
//    public ResponseEntity<Void> removeImage(
//            @PathVariable("id") UUID id,
//            @PathVariable("publicId") String publicId,
//            @RequestBody @Valid List<ProductImageCreateDto> images
//    ) {
//        service.removeImages(id, publicId, images);
//        return ResponseEntity.noContent().build();
//    }
}
