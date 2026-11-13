package br.com.springecommerce.inventory.stock.presentation.http;

import br.com.springecommerce.inventory.stock.application.dto.StockAvailableQuantityUpdateDto;
import br.com.springecommerce.inventory.stock.application.dto.StockCreateDto;
import br.com.springecommerce.inventory.stock.application.dto.StockMinimumQuantityUpdateDto;
import br.com.springecommerce.inventory.stock.application.dto.StockResponseDto;
import br.com.springecommerce.inventory.stock.application.service.StockService;
import br.com.springecommerce.inventory.stock.presentation.docs.StockControllerDocs;
import br.com.springecommerce.inventory.stock.presentation.filter.StockManagerFilter;
import br.com.springecommerce.shared.pagination.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stocks")
@PreAuthorize("hasRole('MANAGER')")
public class StockController implements StockControllerDocs {

    private final StockService commandService;


    public StockController(StockService commandService) {
        this.commandService = commandService;
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StockResponseDto> create(@RequestBody @Valid StockCreateDto dto) {
        var response = commandService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<StockResponseDto>> findAll(
            @ModelAttribute StockManagerFilter filter,
            @PageableDefault(size = 12, sort = "availableQuantity") Pageable pageable
    ) {
        return ResponseEntity.ok().body(commandService.findAll(
                filter, pageable
        ));
    }

    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StockResponseDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok().body(commandService.findById(id));
    }

    @Override
    @PatchMapping(path = "/{id}/minimum-stock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateMinimumQuantity(
            @PathVariable("id") UUID id,
            @RequestBody @Valid StockMinimumQuantityUpdateDto dto) {

        commandService.updateMinimumQuantity(id, dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping(path = "/{id}/increase", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> increase(
            @PathVariable("id") UUID id,
            @RequestBody @Valid StockAvailableQuantityUpdateDto dto) {

        commandService.increase(id, dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping(path = "/{id}/write-off", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> writeOff(
            @PathVariable("id") UUID id,
            @RequestBody @Valid StockAvailableQuantityUpdateDto dto) {

        commandService.writeOff(id, dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping(path = "/{id}/reserve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reserve(
            @PathVariable("id") UUID id,
            @RequestBody @Valid StockAvailableQuantityUpdateDto dto) {

        commandService.reserve(id, dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping(path = "/{id}/release", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> release(
            @PathVariable("id") UUID id,
            @RequestBody @Valid StockAvailableQuantityUpdateDto dto) {

        commandService.release(id, dto);
        return ResponseEntity.noContent().build();
    }

}


