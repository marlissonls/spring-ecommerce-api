package br.com.springecommerce.unittests.sales.support;

import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSnapshotTestDataBuilder {
    private UUID id = UUID.randomUUID();
    private String title = "Product";
    private String productImage = "products/product/image";
    private BigDecimal price = new BigDecimal("100.00");
    private BigDecimal salePrice;
    private Boolean active = true;

    public static ProductSnapshotTestDataBuilder aProduct() {
        return new ProductSnapshotTestDataBuilder();
    }

    public ProductSnapshotTestDataBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public ProductSnapshotTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ProductSnapshotTestDataBuilder withProductImage(String productImage) {
        this.productImage = productImage;
        return this;
    }

    public ProductSnapshotTestDataBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductSnapshotTestDataBuilder withSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
        return this;
    }

    public ProductSnapshotTestDataBuilder withActive(Boolean active) {
        this.active = active;
        return this;
    }

    public ProductSnapshot build() {
        return new ProductSnapshot(
                id,
                title,
                productImage,
                price,
                salePrice,
                active
        );
    }
}
