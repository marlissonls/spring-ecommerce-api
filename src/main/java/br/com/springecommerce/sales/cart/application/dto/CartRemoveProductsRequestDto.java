package br.com.springecommerce.sales.cart.application.dto;

import br.com.springecommerce.catalog.product.domain.exception.ProductErrors;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CartRemoveProductsRequestDto(
        @NotEmpty(message = ProductErrors.Code.PRODUCT_ID_REQUIRED)
        List<@NotNull(message = ProductErrors.Code.PRODUCT_ID_REQUIRED) UUID> productIds
) {}
