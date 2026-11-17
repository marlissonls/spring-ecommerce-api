package br.com.springecommerce.sales.cart.domain.service;

import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;
import br.com.springecommerce.inventory.stock.application.snapshot.StockSnapshot;
import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

//@Component
//public class CartCalculator {
//    public BigDecimal calculateSubtotal(
//            List<CartItemSnapshot> cartItems,
//            Map<UUID, ProductSnapshot> products
//    ) {
//        return cartItems.stream()
//                .map(item -> calculateItemTotal(
//                        item,
//                        products.get(item.productId())
//                ))
//                .flatMap(Optional::stream)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    public Optional<BigDecimal> calculateItemTotal(
//            CartItemSnapshot cartItem,
//            ProductSnapshot product
//    ) {
//        if (product == null || !product.active()) {
//            return Optional.empty();
//        }
//
//        return Optional.of(
//                product.effectivePrice()
//                        .multiply(BigDecimal.valueOf(cartItem.quantity()))
//        );
//    }
//}

@Component
public class CartCalculator {

    public BigDecimal calculateSubtotal(
            List<CartItemSnapshot> cartItems,
            Map<UUID, ProductSnapshot> products,
            Map<UUID, StockSnapshot> stocks
    ) {
        return cartItems.stream()
                .map(item -> calculateItemTotal(
                        item,
                        products.get(item.productId()),
                        stocks.get(item.productId())
                ))
                .flatMap(Optional::stream)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<BigDecimal> calculateItemTotal(
            CartItemSnapshot cartItem,
            ProductSnapshot product,
            StockSnapshot stock
    ) {
        if (!isItemPurchasable(cartItem, product, stock)) {
            return Optional.empty();
        }

        return Optional.of(
                product.effectivePrice()
                        .multiply(BigDecimal.valueOf(cartItem.quantity()))
        );
    }

    public boolean isItemPurchasable(
            CartItemSnapshot cartItem,
            ProductSnapshot product,
            StockSnapshot stock
    ) {
        if (product == null || !product.active()) {
            return false;
        }
        return hasEnoughStock(cartItem, stock);
    }

    public boolean hasEnoughStock(CartItemSnapshot cartItem, StockSnapshot stock) {
        if (stock == null || stock.availableQuantity() == null) {
            return false;
        }
        return stock.availableQuantity() >= cartItem.quantity();
    }
}

