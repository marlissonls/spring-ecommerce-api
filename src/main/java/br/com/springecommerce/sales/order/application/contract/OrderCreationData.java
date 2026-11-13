package br.com.springecommerce.sales.order.application.contract;

import java.util.List;
import java.util.UUID;

public record OrderCreationData(
        UUID customerId,
        List<OrderItemData> items
) {}
