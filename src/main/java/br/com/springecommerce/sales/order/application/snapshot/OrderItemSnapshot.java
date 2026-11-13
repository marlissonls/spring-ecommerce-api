package br.com.springecommerce.sales.order.application.snapshot;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemSnapshot(
    UUID productId,
    String productTitle,
    String productImage,
    BigDecimal price,
    Integer quantity,
    BigDecimal totalPrice
) {}
