package br.com.springecommerce.catalog.product.application.dto;

import br.com.springecommerce.catalog.product.domain.valueobject.ProductImage;

public record ProductImageResponseDto(
        String publicId,
        String url,
        Boolean main,
        Integer position
) {
    public ProductImageResponseDto(ProductImage image) {
        this(
                image.getPublicId(),
                image.getUrl(),
                image.getMain(),
                image.getPosition()
        );
    }
}
