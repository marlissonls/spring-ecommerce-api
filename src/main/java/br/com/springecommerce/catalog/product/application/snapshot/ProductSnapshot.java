package br.com.springecommerce.catalog.product.application.snapshot;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSnapshot(
        UUID id,
        String title,
        String productImage,
        BigDecimal price,
        BigDecimal salePrice,
        Boolean active
) {}