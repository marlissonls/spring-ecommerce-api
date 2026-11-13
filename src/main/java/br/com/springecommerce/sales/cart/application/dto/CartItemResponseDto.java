package br.com.springecommerce.sales.cart.application.dto;

import br.com.springecommerce.sales.cart.domain.status.CartItemAvailability;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponseDto(
        UUID productId,
        String productTitle,
        String productImage,
        BigDecimal price,
        Integer quantity,
        BigDecimal totalPrice,
        CartItemAvailability availability
) {}