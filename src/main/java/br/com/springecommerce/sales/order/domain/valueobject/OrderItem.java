package br.com.springecommerce.sales.order.domain.valueobject;

import br.com.springecommerce.sales.order.application.contract.OrderItemData;
import br.com.springecommerce.sales.order.domain.exception.OrderErrors;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class OrderItem {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_title", nullable = false)
    private String productTitle;

    @Column(name = "product_image", nullable = false, length = 500)
    private String productImage;

    @Positive(message = OrderErrors.Code.ORDER_ITEM_UNIT_PRICE_INVALID)
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Positive(message = OrderErrors.Code.ORDER_ITEM_QUANTITY_INVALID)
    @Column(nullable = false)
    private Integer quantity;

    protected OrderItem() {}

    public OrderItem(
            UUID productId,
            String productTitle,
            String productImage,
            BigDecimal price,
            Integer quantity
    ) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productImage = productImage;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderItem createFrom(OrderItemData item) {
        return new OrderItem(
                item.productId(),
                item.productTitle(),
                item.productImage(),
                item.price(),
                item.quantity()
        );
    }

    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductImage() {
        return productImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem other)) return false;
        return Objects.equals(productId, other.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
