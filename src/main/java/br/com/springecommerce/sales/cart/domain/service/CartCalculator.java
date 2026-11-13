package br.com.springecommerce.sales.cart.domain.service;

import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;
import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CartCalculator {
    public BigDecimal calculateSubtotal(
            List<CartItemSnapshot> cartItems,
            Map<UUID, ProductSnapshot> products
    ) {
        return cartItems.stream()
                .map(item -> calculateItemTotal(
                        item,
                        products.get(item.productId())
                ))
                .flatMap(Optional::stream)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<BigDecimal> calculateItemTotal(
            CartItemSnapshot cartItem,
            ProductSnapshot product
    ) {
        if (product == null || !product.active()) {
            return Optional.empty();
        }

        return Optional.of(
                product.effectivePrice()
                        .multiply(BigDecimal.valueOf(cartItem.quantity()))
        );
    }
}
