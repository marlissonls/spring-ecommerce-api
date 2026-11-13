package br.com.springecommerce.sales.cart.application.snapshot;

import java.util.UUID;

public record CartItemSnapshot(
        UUID productId,
        Integer quantity
) {}