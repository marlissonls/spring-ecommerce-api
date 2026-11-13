package br.com.springecommerce.inventory.stock.domain.model;

import br.com.springecommerce.inventory.stock.domain.exception.InsufficientStockException;
import br.com.springecommerce.inventory.stock.domain.exception.StockErrors;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @Column
    private UUID id;

    @Column(name = "product_id", nullable = false, unique = true)
    private UUID productId;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "minimum_quantity", nullable = false)
    private Integer minimumQuantity;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Stock() {}

    public Stock(
            UUID productId,
            Integer availableQuantity,
            Integer minimumQuantity
    ) {
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.minimumQuantity = minimumQuantity;
    }

    public static Stock createFrom(
            UUID productId,
            Integer availableQuantity,
            Integer minimumQuantity
    ) {
        return new Stock(
                productId, availableQuantity, minimumQuantity
        );
    }

    public void validateAvailableStock(Integer requestedQuantity) {
        if (requestedQuantity > availableQuantity)
            throw new InsufficientStockException(
                    StockErrors.Target.QUANTITY,
                    StockErrors.Code.STOCK_QUANTITY_INSUFFICIENT);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock stock)) return false;
        return Objects.equals(this.getId(), stock.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : 0;
    }
}
