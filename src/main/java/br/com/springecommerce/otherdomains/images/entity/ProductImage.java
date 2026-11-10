//package br.com.springecommerce.otherdomains.images.domain;
//
//import br.com.springecommerce.domain.product.catalog.Product;
//import jakarta.persistence.*;
//
//import java.time.LocalDateTime;
//import java.util.Objects;
//import java.util.UUID;
//
//@Entity
//@Table(name = "product_images")
//public class ProductImage {
//
//    @Id
//    private UUID id;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
//
//    @Column(nullable = false)
//    private String url;
//
//    private String filename;
//
//    @Column(name = "alt_text")
//    private String altText;
//
//    @Column(name = "is_primary")
//    private Boolean isPrimary = Boolean.FALSE;
//
//    @Column(name = "sort_order")
//    private Short sortOrder = 0;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    protected ProductImage() {}
//
//    @PrePersist
//    protected void onCreate() {
//        if (id == null)
//            id = UUID.randomUUID();
//
//        createdAt = LocalDateTime.now();
//    }
//
//    public UUID getId() {
//        return id;
//    }
//
//    public void setId(UUID id) {
//        this.id = id;
//    }
//
//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public String getFilename() {
//        return filename;
//    }
//
//    public void setFilename(String filename) {
//        this.filename = filename;
//    }
//
//    public String getAltText() {
//        return altText;
//    }
//
//    public void setAltText(String altText) {
//        this.altText = altText;
//    }
//
//    public Boolean getPrimary() {
//        return isPrimary;
//    }
//
//    public void setPrimary(Boolean primary) {
//        isPrimary = primary;
//    }
//
//    public Short getSortOrder() {
//        return sortOrder;
//    }
//
//    public void setSortOrder(Short sortOrder) {
//        this.sortOrder = sortOrder;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof ProductImage productImage)) return false;
//        return Objects.equals(this.getId(), productImage.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return id != null ? Objects.hashCode(id) : 0;
//    }
//}
