package br.com.springecommerce.unittests.sales.cart.domain.model;

import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;
import br.com.springecommerce.inventory.stock.application.snapshot.StockSnapshot;
import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import br.com.springecommerce.sales.cart.domain.service.CartCalculator;
import br.com.springecommerce.unittests.sales.support.ProductSnapshotTestDataBuilder;
import br.com.springecommerce.unittests.sales.support.StockSnapshotTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CartCalculatorTests {

    private CartCalculator cartCalculator;

    @BeforeEach
    void setUp() {
        cartCalculator = new CartCalculator();
    }

    @Test
    void shouldCalculateItemTotal() {
        // Given
        CartItemSnapshot item = new CartItemSnapshot(
                UUID.randomUUID(),
                2
        );

        ProductSnapshot product = ProductSnapshotTestDataBuilder
                .aProduct()
                .withPrice(new BigDecimal("100.00"))
                .build();

        StockSnapshot stock = StockSnapshotTestDataBuilder
                .aStock()
                .withProductId(product.id())
                .build();

        // When
        Optional<BigDecimal> result =
                cartCalculator.calculateItemTotal(item, product, stock);

        // Then
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("200.00"), result.get());
    }

    @Test
    void shouldCalculateSubtotalWithMultipleProducts() {
        // Given
        ProductSnapshot firstProduct = ProductSnapshotTestDataBuilder
                .aProduct()
                .withTitle("First Product")
                .withPrice(new BigDecimal("100.00"))
                .build();

        ProductSnapshot secondProduct = ProductSnapshotTestDataBuilder
                .aProduct()
                .withTitle("Second Product")
                .withPrice(new BigDecimal("50.00"))
                .build();

        StockSnapshot firstProductStock = StockSnapshotTestDataBuilder
                .aStock()
                .withProductId(firstProduct.id())
                .build();

        StockSnapshot secondProductStock = StockSnapshotTestDataBuilder
                .aStock()
                .withProductId(secondProduct.id())
                .build();

        List<CartItemSnapshot> items = List.of(
                new CartItemSnapshot(firstProduct.id(), 2),
                new CartItemSnapshot(secondProduct.id(), 3)
        );

        Map<UUID, ProductSnapshot> products = Map.of(
                firstProduct.id(), firstProduct,
                secondProduct.id(), secondProduct
        );

        Map<UUID, StockSnapshot> stocks = Map.of(
                firstProductStock.productId(), firstProductStock,
                secondProductStock.productId(), secondProductStock
        );

        // When
        BigDecimal subtotal =
                cartCalculator.calculateSubtotal(items, products, stocks);

        // Then
        assertEquals(new BigDecimal("350.00"), subtotal);
    }

    @Test
    void shouldReturnZeroWhenCartIsEmpty() {
        // When
        BigDecimal subtotal = cartCalculator.calculateSubtotal(
                List.of(),
                Map.of(),
                Map.of()
        );

        // Then
        assertEquals(BigDecimal.ZERO, subtotal);
    }

    @Test
    void shouldIgnoreInactiveProductsWhenCalculatingSubtotal() {
        // Given
        ProductSnapshot activeProduct = ProductSnapshotTestDataBuilder
                .aProduct()
                .withTitle("Active Product")
                .withPrice(new BigDecimal("100.00"))
                .withActive(true)
                .build();

        ProductSnapshot inactiveProduct = ProductSnapshotTestDataBuilder
                .aProduct()
                .withTitle("Inactive Product")
                .withPrice(new BigDecimal("50.00"))
                .withActive(false)
                .build();

        StockSnapshot activeProductStock = StockSnapshotTestDataBuilder
                .aStock()
                .withProductId(activeProduct.id())
                .build();

        StockSnapshot inactiveProductStock = StockSnapshotTestDataBuilder
                .aStock()
                .withProductId(inactiveProduct.id())
                .build();

        List<CartItemSnapshot> items = List.of(
                new CartItemSnapshot(activeProduct.id(), 2),
                new CartItemSnapshot(inactiveProduct.id(), 3)
        );

        Map<UUID, ProductSnapshot> products = Map.of(
                activeProduct.id(), activeProduct,
                inactiveProduct.id(), inactiveProduct
        );

        Map<UUID, StockSnapshot> stocks = Map.of(
                activeProductStock.productId(), activeProductStock,
                inactiveProductStock.productId(), inactiveProductStock
        );

        // When
        BigDecimal subtotal =
                cartCalculator.calculateSubtotal(items, products, stocks);

        // Then
        assertEquals(new BigDecimal("200.00"), subtotal);
    }

    @Test
    void shouldIgnoreMissingProductsWhenCalculatingSubtotal() {
        // Given
        UUID productId = UUID.randomUUID();

        List<CartItemSnapshot> items = List.of(
                new CartItemSnapshot(productId, 2)
        );

        // When
        BigDecimal subtotal =
                cartCalculator.calculateSubtotal(items, Map.of(), Map.of());

        // Then
        assertEquals(BigDecimal.ZERO, subtotal);
    }

    @Test
    void shouldReturnEmptyWhenProductIsInactive() {
        // Given
        CartItemSnapshot item = new CartItemSnapshot(
                UUID.randomUUID(),
                2
        );

        ProductSnapshot product = ProductSnapshotTestDataBuilder
                .aProduct()
                .withPrice(new BigDecimal("100.00"))
                .withActive(false)
                .build();

        StockSnapshot stock = StockSnapshotTestDataBuilder
                .aStock()
                .withProductId(product.id())
                .build();

        // When
        Optional<BigDecimal> result =
                cartCalculator.calculateItemTotal(item, product, stock);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenProductDoesNotExist() {
        // Given
        CartItemSnapshot item = new CartItemSnapshot(
                UUID.randomUUID(),
                2
        );

        // When
        Optional<BigDecimal> result =
                cartCalculator.calculateItemTotal(item, null, null);

        // Then
        assertTrue(result.isEmpty());
    }
}
