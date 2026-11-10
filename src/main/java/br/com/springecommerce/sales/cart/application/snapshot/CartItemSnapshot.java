package br.com.springecommerce.sales.cart.application.snapshot;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemSnapshot(
        UUID productId,
        String productTitle,
        String productImage,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal totalPrice
) {}