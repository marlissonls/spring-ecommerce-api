package br.com.springecommerce.sales.order.presentation.filter;

import br.com.springecommerce.sales.order.domain.status.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCustomerFilter(
        OrderStatus status,

        BigDecimal minTotal,
        BigDecimal maxTotal,

        LocalDateTime createdFrom,
        LocalDateTime createdTo
) {}