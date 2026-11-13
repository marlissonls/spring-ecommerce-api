package br.com.springecommerce.sales.order.infrastructure.persistence.repository;

import br.com.springecommerce.sales.order.application.snapshot.OrderItemSnapshot;
import br.com.springecommerce.sales.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
        SELECT o
        FROM Order o
        WHERE o.id = :id
          AND o.customerId = :customerId
    """)
    Optional<Order> findByIdAndCustomerId(
        @Param("id") UUID id,
        @Param("customerId") UUID customerId
    );

    Page<Order> findAllByCustomerId(UUID customerId, Pageable pageable);

    @Query("""
        SELECT new br.com.springecommerce.sales.order.application.snapshot.OrderItemSnapshot(
            oi.productId,
            oi.productTitle,
            oi.productImage,
            oi.price,
            oi.quantity,
            oi.price * oi.quantity
        )
        FROM Order o
        JOIN o.items oi
        WHERE o.id = :orderId
    """)
    List<OrderItemSnapshot> findOrderItems(@Param("orderId") UUID orderId);
}
