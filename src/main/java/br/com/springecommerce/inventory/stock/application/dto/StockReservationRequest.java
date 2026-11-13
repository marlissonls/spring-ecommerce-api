package br.com.springecommerce.inventory.stock.application.dto;

import java.util.UUID;

public record StockReservationRequest(
        UUID productId,
        Integer quantity
) {}
