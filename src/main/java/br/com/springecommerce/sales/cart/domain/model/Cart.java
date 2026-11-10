package br.com.springecommerce.sales.cart.domain.model;

import br.com.springecommerce.sales.cart.domain.exception.CartErrors;
import br.com.springecommerce.sales.cart.domain.status.CartStatus;
import br.com.springecommerce.sales.cart.domain.valueobject.CartItem;
import br.com.springecommerce.sales.cart.domain.exception.CartItemNotFoundException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @Column
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Valid
    @ElementCollection
    @CollectionTable(
            name = "cart_items",
            joinColumns = @JoinColumn(name = "cart_id")
    )
    private Set<CartItem> items = new HashSet<>();

    @PositiveOrZero
    @Column(name = "subtotal_amount", nullable = false)
    private BigDecimal subtotalAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CartStatus status = CartStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Cart() {}

    public Cart(UUID customerId) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
    }

    public static Cart createFrom(UUID customerId) {
        return new Cart(customerId);
    }

    public void markAsConverted() {
        this.status = CartStatus.CONVERTED;
    }

    public void addItem(
            UUID productId,
            String productTitle,
            String productImage,
            BigDecimal unitPrice,
            Integer quantity
    ) {
        CartItem existingItem = items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(quantity);
            existingItem.setUnitPrice(unitPrice);

        } else {
            items.add(new CartItem(
                    productId,
                    productTitle,
                    productImage,
                    unitPrice,
                    quantity
            ));
        }

        recalculateTotals();
    }

    public void removeItem(UUID productId) {
        boolean removed = items.removeIf(
                item -> item.getProductId().equals(productId)
        );

        if (!removed) {
            throw new CartItemNotFoundException(
                    CartErrors.Target.ITEM,
                    CartErrors.Code.CART_ITEM_NOT_FOUND
            );
        }

        recalculateTotals();
    }

    private void recalculateTotals() {

        subtotalAmount = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Set<CartItem> getItems() {
        return Collections.unmodifiableSet(items);
    }

    public BigDecimal getSubtotalAmount() {
        return subtotalAmount;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
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
        if (!(o instanceof Cart cart)) return false;
        return Objects.equals(this.getId(), cart.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : 0;
    }
}
