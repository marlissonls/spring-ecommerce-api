package br.com.springecommerce.unittests.sales.cart.domain.model;

import br.com.springecommerce.sales.cart.domain.exception.CartItemNotFoundException;
import br.com.springecommerce.sales.cart.domain.model.Cart;
import br.com.springecommerce.sales.cart.domain.status.CartStatus;
import br.com.springecommerce.sales.cart.domain.valueobject.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CartTests {
    private UUID customerId;
    private Cart cart;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        cart = Cart.createFrom(customerId);
    }

    @Test
    void shouldCreateAnActiveCartWithEmptyItemsAndZeroSubtotal() {
        // Given
        UUID customerId = UUID.randomUUID();

        // When
        Cart cart = Cart.createFrom(customerId);

        // Then
        assertNotNull(cart.getId());
        assertEquals(customerId, cart.getCustomerId());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getSubtotalAmount());
        assertEquals(CartStatus.ACTIVE, cart.getStatus());
    }

    @Test
    void shouldAddProductAndCalculateSubtotal() {
        // Given
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;

        // When
        cart.addItem(
                productId,
                productTitle,
                productImage,
                unitPrice,
                quantity
        );

        // Then
        assertEquals(1, cart.getItems().size());

        CartItem item = cart.getItems().iterator().next();

        assertEquals(productId, item.getProductId());
        assertEquals(unitPrice, item.getUnitPrice());
        assertEquals(quantity, item.getQuantity());
        assertEquals(
                new BigDecimal("200.00"),
                cart.getSubtotalAmount()
        );
    }

    @Test
    void shouldAddMultipleProductsAndCalculateSubtotal() {
        // Given
        UUID firstProductId = UUID.randomUUID();
        String firstProductTitle = "Barraca Camping";
        String firstProductImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal firstProductUnitPrice = new BigDecimal("100.00");
        int firstProductQuantity = 2;

        UUID secondProductId = UUID.randomUUID();
        String secondProductTitle = "Bota Trekking";
        String secondProductImage = "products/bota-trekking/ciyya0m68tobdemmgmkw";
        BigDecimal secondProductUnitPrice = new BigDecimal("50.00");
        int secondProductQuantity = 3;

        BigDecimal total = firstProductUnitPrice.multiply(BigDecimal.valueOf(firstProductQuantity))
                .add(secondProductUnitPrice.multiply(BigDecimal.valueOf(secondProductQuantity)));

        // When
        cart.addItem(
                firstProductId,
                firstProductTitle,
                firstProductImage,
                firstProductUnitPrice,
                firstProductQuantity
        );
        cart.addItem(
                secondProductId,
                secondProductTitle,
                secondProductImage,
                secondProductUnitPrice,
                secondProductQuantity
        );

        // Then
        assertEquals(2, cart.getItems().size());
        assertEquals(
                total,
                cart.getSubtotalAmount()
        );
    }

    @Test
    void shouldUpdateExistingProductQuantityAndPrice() {
        // Given
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal oldUnitPrice = new BigDecimal("100.00");
        BigDecimal newUnitPrice = new BigDecimal("80.00");

        cart.addItem(
                productId,
                productTitle,
                productImage,
                oldUnitPrice,
                2
        );

        // When
        cart.addItem(
                productId,
                productTitle,
                productImage,
                newUnitPrice,
                3
        );

        // Then
        assertEquals(1, cart.getItems().size());

        CartItem item = cart.getItems().iterator().next();

        assertEquals(productId, item.getProductId());
        assertEquals(newUnitPrice, item.getUnitPrice());
        assertEquals(3, item.getQuantity());
        assertEquals(new BigDecimal("240.00"), cart.getSubtotalAmount());
    }

    @Test
    void shouldRemoveProductAndRecalculateSubtotal() {
        // Given
        UUID firstProductId = UUID.randomUUID();
        String firstProductTitle = "Barraca Camping";
        String firstProductImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal firstProductUnitPrice = new BigDecimal("100.00");
        int firstProductQuantity = 2;

        UUID secondProductId = UUID.randomUUID();
        String secondProductTitle = "Bota Trekking";
        String secondProductImage = "products/bota-trekking/ciyya0m68tobdemmgmkw";
        BigDecimal secondProductUnitPrice = new BigDecimal("50.00");
        int secondProductQuantity = 3;

        cart.addItem(
                firstProductId,
                firstProductTitle,
                firstProductImage,
                firstProductUnitPrice,
                firstProductQuantity
        );

        cart.addItem(
                secondProductId,
                secondProductTitle,
                secondProductImage,
                secondProductUnitPrice,
                secondProductQuantity
        );

        BigDecimal remainingProductTotal
                = secondProductUnitPrice.multiply(BigDecimal.valueOf(secondProductQuantity));

        // When
        cart.removeItem(firstProductId);

        // Then
        assertEquals(1, cart.getItems().size());
        assertEquals(
                secondProductId,
                cart.getItems().iterator().next().getProductId()
        );
        assertEquals(remainingProductTotal, cart.getSubtotalAmount());
    }

    @Test
    void shouldThrowWhenRemovingAProductThatIsNotInTheCart() {
        // Given
        UUID productId = UUID.randomUUID();

        // When / Then
        assertThrows(
                CartItemNotFoundException.class,
                () -> cart.removeItem(productId)
        );
    }

    @Test
    void shouldRemoveLastProductAndResetSubtotal() {
        // Given
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal productPrice = new BigDecimal("100.00");

        cart.addItem(
                productId,
                productTitle,
                productImage,
                productPrice,
                2
        );

        // When
        cart.removeItem(productId);

        // Then
        assertTrue(cart.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getSubtotalAmount());
    }

    @Test
    void shouldMarkCartAsConverted() {
        // Given
        assertEquals(CartStatus.ACTIVE, cart.getStatus());

        // When
        cart.markAsConverted();

        // Then
        assertEquals(CartStatus.CONVERTED, cart.getStatus());
    }
}
