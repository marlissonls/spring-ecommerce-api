package br.com.springecommerce.catalog.product.application.dto;

import br.com.springecommerce.catalog.product.domain.model.Product;
import br.com.springecommerce.catalog.product.domain.valueobject.ProductImage;
import br.com.springecommerce.inventory.stock.application.snapshot.StockSnapshot;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record ProductManagerDetailsResponseDto(
        UUID id,
        String title,
        String slug,
        String barcode,
        String sku,
        String description,
        BigDecimal price,
        BigDecimal salePrice,
        BigDecimal costPrice,
        Integer stock,
        Integer reservedStock,
        Integer minimumStock,
        BigDecimal rating,
        Integer ratingCount,
        Boolean active,
        Boolean featured,
        List<ProductImage> images,
        UUID categoryId,
        UUID brandId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ProductManagerDetailsResponseDto(Product product, List<ProductImage> images, Optional<StockSnapshot> stock) {
        this(
            product.getId(),
            product.getTitle(),
            product.getSlug(),
            product.getBarcode(),
            product.getSku(),
            product.getDescription(),
            product.getPrice(),
            product.getSalePrice(),
            product.getCostPrice(),
            stock.map(StockSnapshot::availableQuantity).orElse(null),
            stock.map(StockSnapshot::reservedQuantity).orElse(null),
            stock.map(StockSnapshot::minimumQuantity).orElse(null),
            product.getRating(),
            product.getRatingCount(),
            product.getActive(),
            product.getFeatured(),
            images,
            product.getCategoryId(),
            product.getBrandId(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}