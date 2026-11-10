package br.com.springecommerce.catalog.product.application.snapshot;

import br.com.springecommerce.inventory.stock.domain.model.Stock;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductStockSnapshot(
        UUID id,
        String title,
        String productImage,
        BigDecimal price,
        BigDecimal salePrice,
        Boolean active,
        Integer stock

) {
    public ProductStockSnapshot(ProductSnapshot product, Integer stock) {
        this(
                product.id(),
                product.title(),
                product.productImage(),
                product.price(),
                product.salePrice(),
                product.active(),
                stock
        );
    }

    public BigDecimal effectivePrice() {
        return salePrice != null && salePrice.compareTo(price) < 0
                ? salePrice
                : price;
    }
}
