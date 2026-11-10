//package br.com.springecommerce.otherdomains.customer.favorite.domain;
//
//import br.com.springecommerce.domain.product.catalog.Product;
//import br.com.springecommerce.domain.user.iam.User;
//import jakarta.persistence.*;
//
//import java.time.LocalDateTime;
//import java.util.Objects;
//
//@Entity
//@Table(name = "favorites")
//public class Favorite {
//
//    @EmbeddedId
//    private FavoriteId id;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @MapsId("customerId")
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @MapsId("productId")
//    @JoinColumn(name = "product_id")
//    private Product product;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    protected Favorite() {}
//
//    public Favorite(User user, Product product) {
//        this.user = user;
//        this.product = product;
//        this.id = new FavoriteId(user.getId(), product.getId());
//    }
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//    }
//
//    public FavoriteId getId() {
//        return id;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public Product getProduct() {
//        return product;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Favorite favorite)) return false;
//        return Objects.equals(this.getId(), favorite.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return id != null ? Objects.hashCode(id) : 0;
//    }
//}
