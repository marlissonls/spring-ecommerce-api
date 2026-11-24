package br.com.springecommerce.sales.order.infrastructure.specification;

import br.com.springecommerce.sales.order.domain.model.Order;
import br.com.springecommerce.sales.order.domain.status.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderSpecifications {

    private OrderSpecifications() {}

    public static Specification<Order> customerId(UUID customerId) {
        return (root, query, cb) ->
                customerId == null
                        ? null
                        : cb.equal(root.get("customerId"), customerId);
    }

    public static Specification<Order> status(OrderStatus status) {
        return (root, query, cb) ->
                status == null
                        ? null
                        : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> minTotal(BigDecimal minTotal) {
        return (root, query, cb) ->
                minTotal == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.get("totalAmount"),
                        minTotal
                );
    }

    public static Specification<Order> maxTotal(BigDecimal maxTotal) {
        return (root, query, cb) ->
                maxTotal == null
                        ? null
                        : cb.lessThanOrEqualTo(
                        root.get("totalAmount"),
                        maxTotal
                );
    }

    public static Specification<Order> createdFrom(LocalDateTime createdFrom) {
        return (root, query, cb) ->
                createdFrom == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        createdFrom
                );
    }

    public static Specification<Order> createdTo(LocalDateTime createdTo) {
        return (root, query, cb) ->
                createdTo == null
                        ? null
                        : cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        createdTo
                );
    }

    public static Specification<Order> updatedFrom(LocalDateTime updatedFrom) {
        return (root, query, cb) ->
                updatedFrom == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.get("updatedAt"),
                        updatedFrom
                );
    }

    public static Specification<Order> updatedTo(LocalDateTime updatedTo) {
        return (root, query, cb) ->
                updatedTo == null
                        ? null
                        : cb.lessThanOrEqualTo(
                        root.get("updatedAt"),
                        updatedTo
                );
    }

//    public static Specification<Order> search(String search) {
//        return (root, query, cb) -> {
//
//            if (search == null || search.isBlank()) {
//                return null;
//            }
//
//            String term = "%" + search.toLowerCase().trim() + "%";
//
//            Join<Order, Customer> customer = root.join("customer", JoinType.LEFT);
//
//            List<Predicate> predicates = new ArrayList<>();
//
//            predicates.add(
//                    cb.like(
//                            cb.lower(customer.get("name")),
//                            term
//                    )
//            );
//
//            predicates.add(
//                    cb.like(
//                            cb.lower(customer.get("email")),
//                            term
//                    )
//            );
//
//            // Pesquisa pelo ID do pedido
//            try {
//                UUID orderId = UUID.fromString(search.trim());
//
//                predicates.add(
//                        cb.equal(root.get("id"), orderId)
//                );
//            } catch (IllegalArgumentException ignored) {
//                // O termo não representa um UUID.
//            }
//
//            return cb.or(predicates.toArray(new Predicate[0]));
//        };
//    }
}