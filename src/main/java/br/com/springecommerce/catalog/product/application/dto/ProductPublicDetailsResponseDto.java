package br.com.springecommerce.catalog.product.application.dto;

import br.com.springecommerce.catalog.product.domain.model.Product;
import br.com.springecommerce.catalog.product.domain.valueobject.ProductImage;
import br.com.springecommerce.inventory.stock.application.snapshot.StockSnapshot;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record ProductPublicDetailsResponseDto(
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
        List<ProductImage> images,
        UUID categoryId,
        UUID brandId
) {
    public ProductPublicDetailsResponseDto(Product product, List<ProductImage> images, Optional<StockSnapshot> stock) {
        this(
            product.getId(),
            product.getTitle(),
            product.getSlug(),
            product.getDescription(),
            product.getPrice(),
            product.getSalePrice(),
            stock.map(StockSnapshot::availableQuantity).orElse(null),
            product.getFeatured(),
            product.getRating(),
            product.getRatingCount(),
            images,
            product.getCategoryId(),
            product.getBrandId()
        );
    }
}