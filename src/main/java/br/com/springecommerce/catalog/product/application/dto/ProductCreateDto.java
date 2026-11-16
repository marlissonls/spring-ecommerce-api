package br.com.springecommerce.catalog.product.application.dto;

import br.com.springecommerce.catalog.product.domain.exception.ProductErrors;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductCreateDto(

        @NotBlank(message = ProductErrors.Code.PRODUCT_TITLE_REQUIRED)
        @Size(min = 3, max = 255, message = ProductErrors.Code.PRODUCT_TITLE_SIZE)
        String title,

        @NotBlank(message = ProductErrors.Code.PRODUCT_DESCRIPTION_REQUIRED)
        @Size(max = 2000, message = ProductErrors.Code.PRODUCT_DESCRIPTION_SIZE)
        String description,

        @NotNull(message = ProductErrors.Code.PRODUCT_PRICE_REQUIRED)
        @Positive(message = ProductErrors.Code.PRODUCT_PRICE_INVALID)
        BigDecimal price,

        @PositiveOrZero(message = ProductErrors.Code.PRODUCT_SALE_PRICE_INVALID)
        BigDecimal salePrice,

        @PositiveOrZero(message = ProductErrors.Code.PRODUCT_COST_PRICE_INVALID)
        BigDecimal costPrice,

        @Size(max = 50, message = ProductErrors.Code.PRODUCT_BARCODE_SIZE)
        String barcode,

        @Size(max = 100, message = ProductErrors.Code.PRODUCT_SKU_SIZE)
        String sku,

        @NotNull(message = ProductErrors.Code.PRODUCT_CATEGORY_REQUIRED)
        UUID categoryId,

        @NotNull(message = ProductErrors.Code.PRODUCT_BRAND_REQUIRED)
        UUID brandId,

        Integer initialStock,

        Integer minimumStock
) {}
