//package br.com.springecommerce.otherdomains.customer.review.domain;
//
//import br.com.springecommerce.domain.product.catalog.Product;
//import br.com.springecommerce.domain.user.iam.User;
//import jakarta.persistence.*;
//
//import java.math.BigDecimal;
//
//import java.time.OffsetDateTime;
//import java.util.Objects;
//
////@Entity
////@Table(name = "reviews")
//public class Review {
//
//    @EmbeddedId
//    private ReviewId id;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @MapsId("customerId")
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @MapsId("productId")
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
//
//    @Column(nullable = false, precision = 3, scale = 2)
//    //@DecimalMin(value = "0.00", message = "A nota mínima é 0.00")
//    //@DecimalMax(value = "5.00", message = "A nota máxima é 5.00")
//    private BigDecimal rating = BigDecimal.ZERO;
//
//    @Column(columnDefinition = "TEXT")
//    private String comment;
//
//    @Column(nullable = false)
//    private Boolean hidden = Boolean.FALSE;
//
//    @Column(name = "created_at", updatable = false)
//    private OffsetDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private OffsetDateTime updatedAt;
//
//    protected Review() {}
//
//    public Review(User user, Product product) {
//        this.user = user;
//        this.product = product;
//        this.id = new ReviewId(user.getId(), product.getId());
//    }
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = OffsetDateTime.now();
//        updatedAt = createdAt;
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = OffsetDateTime.now();
//    }
//
//    public ReviewId getId() {
//        return id;
//    }
//
//    public void setId(ReviewId id) {
//        this.id = id;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
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
//    public BigDecimal getRating() {
//        return rating;
//    }
//
//    public void setRating(BigDecimal rating) {
//        this.rating = rating;
//    }
//
//    public String getComment() {
//        return comment;
//    }
//
//    public void setComment(String comment) {
//        this.comment = comment;
//    }
//
//    public Boolean getHidden() {
//        return hidden;
//    }
//
//    public void setHidden(Boolean hidden) {
//        this.hidden = hidden;
//    }
//
//    public OffsetDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(OffsetDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public OffsetDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(OffsetDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Review review)) return false;
//        return Objects.equals(this.getId(), review.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return id != null ? Objects.hashCode(id) : 0;
//    }
//}
