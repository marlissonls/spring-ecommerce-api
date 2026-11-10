//package br.com.springecommerce.otherdomains.customer.favorite.domain;
//
//import jakarta.persistence.*;
//
//import java.io.Serializable;
//import java.util.Objects;
//import java.util.UUID;
//
//@Embeddable
//public class FavoriteId implements Serializable {
//
//    @Column(name = "user_id")
//    private UUID userId;
//
//    @Column(name = "product_id")
//    private UUID productId;
//
//    protected FavoriteId() {}
//
//    public FavoriteId(UUID userId, UUID productId) {
//        this.userId = userId;
//        this.productId = productId;
//    }
//
//    public UUID getUserId() {
//        return userId;
//    }
//
//    public UUID getProductId() {
//        return productId;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof FavoriteId favoriteId)) return false;
//        return Objects.equals(userId, favoriteId.userId)
//                && Objects.equals(productId, favoriteId.productId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(userId, productId);
//    }
//}
