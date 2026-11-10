package br.com.springecommerce.integrationtests.fixture;

import br.com.springecommerce.catalog.brand.domain.model.Brand;
import br.com.springecommerce.catalog.category.domain.model.Category;
import br.com.springecommerce.catalog.product.application.dto.ProductImageCreateDto;
import br.com.springecommerce.catalog.product.domain.model.Product;
import br.com.springecommerce.catalog.product.infrastructure.persistence.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductFixture {

    private final ProductRepository repository;

    private String title = "Produto Teste";
    private String slug = "produto-teste";
    private String description = "Descrição do produto";
    private BigDecimal price = new BigDecimal("10.00");
    private BigDecimal salePrice = new BigDecimal("10.00");
    private BigDecimal costPrice = new BigDecimal("10.00");
    private String barcode = "7891234567890";
    private String sku = "SKU-001";
    private Category category;
    private Brand brand;

    private String publicId = "products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw";
    private String url = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
    private Integer position = 0;
    private Boolean main = true;

    public ProductFixture(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductFixture withTitle(String title) {
        this.title = title;
        return this;
    }

    public ProductFixture withSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public ProductFixture withDescription(String description) {
        this.description = description;
        return this;
    }

    public ProductFixture withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductFixture withSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
        return this;
    }

    public ProductFixture withCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
        return this;
    }

    public ProductFixture withBarcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    public ProductFixture withSku(String sku) {
        this.sku = sku;
        return this;
    }

    public ProductFixture withCategory(Category category) {
        this.category = category;
        return this;
    }

    public ProductFixture withBrand(Brand brand) {
        this.brand = brand;
        return this;
    }

    public ProductFixture withImagePublicId(String publicId) {
        this.publicId = publicId;
        return this;
    }

    public ProductFixture withImageUrl(String url) {
        this.url = url;
        return this;
    }

    public ProductFixture withImagePosition(Integer position) {
        this.position = position;
        return this;
    }

    public ProductFixture withImageMain(Boolean main) {
        this.main = main;
        return this;
    }

    public Product create() {
        Product product = new Product(
                title,
                slug,
                description,
                price,
                salePrice,
                costPrice,
                barcode,
                sku,
                category,
                brand
        );

        var imageDto = new ProductImageCreateDto(
                publicId,
                url,
                position,
                main
        );

        product.addImages(List.of(imageDto));

        return repository.save(product);
    }

    public void deactivate(Product product) {
        product.setActive(false);
        repository.save(product);
    }

    public void activate(Product product) {
        product.setActive(false);
        repository.save(product);
    }

    public void updateSalePrice(Product product, BigDecimal updatedSalePrice) {
        product.setSalePrice(updatedSalePrice);
        repository.save(product);
    }
}

