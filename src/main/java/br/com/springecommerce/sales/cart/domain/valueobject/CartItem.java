package br.com.springecommerce.sales.cart.domain.valueobject;

import br.com.springecommerce.sales.cart.domain.exception.CartErrors;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CartItem {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_title", nullable = false)
    private String productTitle;

    @Column(name = "product_image", nullable = false)
    private String productImage;

    @Positive(message = CartErrors.Code.CART_ITEM_UNIT_PRICE_INVALID)
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Positive(message = CartErrors.Code.CART_ITEM_QUANTITY_INVALID)
    @Column(nullable = false)
    private Integer quantity;



    protected CartItem() {}

    public CartItem(
            UUID productId,
            String productTitle,
            String productImage,
            BigDecimal unitPrice,
            Integer quantity
    ) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productImage = productImage;
        this.unitPrice = unitPrice;
        this.quantity = quantity;

    }

    public BigDecimal getTotalPrice() {

        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String image) {
        this.productImage = image;
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