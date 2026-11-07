package br.com.springecommerce.catalog.product.application.dto;

import br.com.springecommerce.catalog.product.domain.model.Product;
import br.com.springecommerce.catalog.product.valueobject.ProductImage;
import br.com.springecommerce.inventory.stock.domain.model.Stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductPublicResponseDto(
        UUID id,
        String title,
        String slug,
        String description,
        BigDecimal price,
        BigDecimal salePrice,
        Integer stock,
        Boolean featured,
        BigDecimal rating,
        Integer ratingCount,
        String imageUrl,
        UUID categoryId,
        UUID brandId
) {
    public ProductPublicResponseDto(Product product, ProductImage image, Stock stock) {
        this(
            product.getId(),
            product.getTitle(),
            product.getSlug(),
            product.getDescription(),
            product.getPrice(),
            product.getSalePrice(),
            stock.getAvailableQuantity(),
            product.getFeatured(),
            product.getRating(),
            product.getRatingCount(),
            image.getImageUrl(),
            product.getCategoryId(),
            product.getBrandId()
        );
    }
}