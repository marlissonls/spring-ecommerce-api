package br.com.springecommerce.sales.order.presentation.filter;

import br.com.springecommerce.sales.order.domain.status.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderManagerFilter(
//        String search,

        UUID customerId,

        OrderStatus status,

        BigDecimal minTotal,
        BigDecimal maxTotal,

        LocalDateTime createdFrom,
        LocalDateTime createdTo,

        LocalDateTime updatedFrom,
        LocalDateTime updatedTo
) {}