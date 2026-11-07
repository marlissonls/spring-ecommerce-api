package br.com.springecommerce.catalog.product.domain.model;

import br.com.springecommerce.catalog.brand.domain.model.Brand;
import br.com.springecommerce.catalog.category.domain.model.Category;
import br.com.springecommerce.catalog.product.application.dto.ProductCreateDto;
import br.com.springecommerce.catalog.product.application.dto.ProductImageCreateDto;
import br.com.springecommerce.catalog.product.application.dto.ProductUpdateDto;
import br.com.springecommerce.catalog.product.domain.valueobject.ProductImage;
import br.com.springecommerce.inventory.stock.domain.model.Stock;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    private UUID id;

    @Column(name = "category_id", insertable = false, updatable = false)
    private UUID categoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "brand_id", insertable = false, updatable = false)
    private UUID brandId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(nullable = false, unique = true, length = 50)
    private String barcode;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean featured = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Stock> stocks = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = "product_id")
    )
    private Set<ProductImage> images = new HashSet<>();

    protected Product() {}

    public Product(
            String title,
            String slug,
            String description,
            BigDecimal price,
            BigDecimal salePrice,
            BigDecimal costPrice,
            String barcode,
            String sku,
            Category category,
            Brand brand
    ) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.salePrice = salePrice;
        this.costPrice = costPrice;
        this.barcode = barcode;
        this.sku = sku;
        this.category = category;
        this.brand = brand;
    }

    public static Product createFrom(
            ProductCreateDto dto,
            String slug,
            Category category,
            Brand brand)
    {
        return new Product(
            dto.title(),
            slug,
            dto.description(),
            dto.price(),
            dto.salePrice(),
            dto.costPrice(),
            dto.barcode(),
            dto.sku(),
            category,
            brand
        );
    }

    public void update(ProductUpdateDto dto, Category category, Brand brand) {

        if (dto.title() != null && !dto.title().isBlank())
            this.setTitle(dto.title());

        if (dto.description() != null && !dto.description().isBlank())
            this.setDescription(dto.description());

        if (dto.price() != null)
            this.setPrice(dto.price());

        if (dto.salePrice() != null)
            this.setSalePrice(dto.salePrice());

        if (dto.costPrice() != null)
            this.setCostPrice(dto.costPrice());

        if (dto.sku() != null && !dto.sku().isBlank())
            this.setSku(dto.sku());

        if (category != null) {
            this.setCategoryId(dto.categoryId());
            this.setCategory(category);
        }

        if (brand != null) {
            this.setBrandId(dto.brandId());
            this.setBrand(brand);
        }
    }

    public void addImages(List<ProductImageCreateDto> imagesDto) {
        for (ProductImageCreateDto dto : imagesDto) {
            ProductImage productImage = new ProductImage(
                    dto.publicId(),
                    dto.url(),
                    dto.position(),
                    dto.main()
            );

            images.add(productImage);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(this.getId(), product.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : 0;
    }
}
