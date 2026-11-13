package br.com.springecommerce.sales.cart.domain.valueobject;

import br.com.springecommerce.sales.cart.domain.exception.CartErrors;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CartItem {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Positive(message = CartErrors.Code.CART_ITEM_QUANTITY_INVALID)
    @Column(nullable = false)
    private Integer quantity;

    protected CartItem() {}

    public CartItem(
            UUID productId,
            Integer quantity
    ) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem other)) return false;
        return Objects.equals(productId, other.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}