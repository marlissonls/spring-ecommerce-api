package br.com.springecommerce.sales.cart.infrastructure.persistence.repository;

import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import br.com.springecommerce.sales.cart.domain.model.Cart;
import br.com.springecommerce.sales.cart.domain.status.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    @Query("""
        SELECT c
        FROM Cart c
        WHERE c.customerId = :customerId
           AND c.status = :status
    """)
    Optional<Cart> findByCustomerIdAndStatus(
            @Param("customerId") UUID customerId,
            @Param("status") CartStatus status
    );

    @Query("""
    SELECT new br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot(
        ci.productId,
        ci.productTitle,
        ci.productImage,
        ci.unitPrice,
        ci.quantity,
        ci.unitPrice * ci.quantity
    )
    FROM Cart c
    JOIN c.items ci
    WHERE c.id = :cartId
    """)
    List<CartItemSnapshot> findCartItems(@Param("cartId") UUID cartId);
}