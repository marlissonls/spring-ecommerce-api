package br.com.springecommerce.sales.order.application.dto;

import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import br.com.springecommerce.sales.order.application.snapshot.OrderItemSnapshot;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponseDto (
        UUID productId,
        String productTitle,
        String image,
        BigDecimal price,
        Integer quantity,
        BigDecimal totalPrice
) {
    public OrderItemResponseDto(OrderItemSnapshot item) {
        this(
                item.productId(),
                item.productTitle(),
                item.productImage(),
                item.price(),
                item.quantity(),
                item.totalPrice()
        );
    }
}