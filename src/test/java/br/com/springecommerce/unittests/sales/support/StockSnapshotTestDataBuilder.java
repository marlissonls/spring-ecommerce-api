package br.com.springecommerce.unittests.sales.support;

import br.com.springecommerce.inventory.stock.application.snapshot.StockSnapshot;

import java.util.UUID;

public class StockSnapshotTestDataBuilder {
    private UUID id = UUID.randomUUID();
    private UUID productId = UUID.randomUUID();
    private Integer availableQuantity = 20;
    private Integer reservedQuantity = 0;
    private Integer minimumQuantity = 5;

    public static StockSnapshotTestDataBuilder aStock() {
        return new StockSnapshotTestDataBuilder();
    }

    public StockSnapshotTestDataBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public StockSnapshotTestDataBuilder withProductId(UUID productId) {
        this.productId = productId;
        return this;
    }

    public StockSnapshotTestDataBuilder withAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
        return this;
    }

    public StockSnapshotTestDataBuilder withReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
        return this;
    }

    public StockSnapshotTestDataBuilder withMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
        return this;
    }

    public StockSnapshot build() {
        return new StockSnapshot(
                id,
                productId,
                availableQuantity,
                reservedQuantity,
                minimumQuantity
        );
    }
}
