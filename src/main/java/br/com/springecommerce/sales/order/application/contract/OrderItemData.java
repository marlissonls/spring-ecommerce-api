package br.com.springecommerce.sales.order.application.contract;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemData(
        UUID productId,
        String productTitle,
        String productImage,
        BigDecimal price,
        Integer quantity
) {}
