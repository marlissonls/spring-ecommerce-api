package br.com.springecommerce.sales.order.domain.model;

import br.com.springecommerce.sales.order.application.contract.OrderCreationData;
import br.com.springecommerce.sales.order.application.contract.OrderItemData;
import br.com.springecommerce.sales.order.domain.exception.OrderErrors;
import br.com.springecommerce.sales.order.domain.status.OrderStatus;
import br.com.springecommerce.sales.order.domain.valueobject.OrderItem;
import br.com.springecommerce.shared.exception.base.ConflictException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Valid
    @ElementCollection
    @CollectionTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id")
    )
    private Set<OrderItem> items = new HashSet<>();

    @PositiveOrZero
    @Column(name = "subtotal_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalAmount = BigDecimal.ZERO;

    @PositiveOrZero
    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @PositiveOrZero
    @Column(name = "shipping_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingAmount = BigDecimal.ZERO;

    @PositiveOrZero
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Order() {}

    public Order(UUID customerId) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
    }

    public static Order createFrom(OrderCreationData data) {
        Order order = new Order(data.customerId());

        data.items().forEach(order::addItem);

        order.recalculateSubtotal();
        order.recalculateTotal();

        return order;
    }

    private void recalculateSubtotal() {
        this.subtotalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void recalculateTotal() {
        this.totalAmount = subtotalAmount;
//                .add(shippingAmount)
//                .subtract(discountAmount);
    }

    public void addItem(OrderItemData item) {
        items.add(OrderItem.createFrom(item));
    }

    public void markAsCancelled() {
        if (!status.canBeCancelled()) {
            throw new ConflictException(
                    OrderErrors.Target.STATUS,
                    OrderErrors.Code.ORDER_CANNOT_BE_CANCELLED
            );
        }

        status = OrderStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public @Valid Set<OrderItem> getItems() {
        return items;
    }

    public @PositiveOrZero BigDecimal getSubtotalAmount() {
        return subtotalAmount;
    }

    public @PositiveOrZero BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public @PositiveOrZero BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public @PositiveOrZero BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order cart)) return false;
        return Objects.equals(this.getId(), cart.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : 0;
    }
}
