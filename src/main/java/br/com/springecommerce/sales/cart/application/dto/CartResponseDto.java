package br.com.springecommerce.sales.cart.application.dto;

import br.com.springecommerce.sales.cart.domain.model.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponseDto(
        UUID id,
        UUID customerId,
        BigDecimal subTotalAmount,
        List<CartItemResponseDto> items
) {
    public CartResponseDto(Cart cart, BigDecimal subTotalAmount, List<CartItemResponseDto> items) {
        this(
                cart.getId(),
                cart.getCustomerId(),
                subTotalAmount,
                items
        );
    }
}
