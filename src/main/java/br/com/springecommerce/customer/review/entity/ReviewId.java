//package br.com.springecommerce.otherdomains.customer.review.domain;
//
//import jakarta.persistence.*;
//import jakarta.persistence.Embeddable;
//
//import java.io.Serializable;
//import java.util.Objects;
//import java.util.UUID;
//
//@Embeddable
//public class ReviewId implements Serializable {
//
//    @Column(name = "user_id")
//    private UUID userId;
//
//    @Column(name = "product_id")
//    private UUID productId;
//
//    protected ReviewId() {}
//
//    public ReviewId(UUID userID, UUID productId) {
//        this.userId = userID;
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
//        if (!(o instanceof ReviewId reviewId)) return false;
//        return Objects.equals(userId, reviewId.userId) &&
//                Objects.equals(productId, reviewId.productId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(userId, productId);
//    }
//}
