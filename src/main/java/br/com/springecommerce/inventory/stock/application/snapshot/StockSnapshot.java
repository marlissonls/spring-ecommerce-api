package br.com.springecommerce.inventory.stock.application.snapshot;

import br.com.springecommerce.inventory.stock.domain.model.Stock;

import java.util.UUID;

public record StockSnapshot(
        UUID id,
        UUID productId,
        Integer availableQuantity,
        Integer reservedQuantity,
        Integer minimumQuantity
) {
    public StockSnapshot(Stock stock) {
        this(
                stock.getId(),
                stock.getProductId(),
                stock.getAvailableQuantity(),
                stock.getReservedQuantity(),
                stock.getMinimumQuantity()
        );
    }
}
