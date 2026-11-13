package br.com.springecommerce.unittests.sales.cart.domain.model;

import br.com.springecommerce.sales.cart.domain.exception.CartItemNotFoundException;
import br.com.springecommerce.sales.cart.domain.model.Cart;
import br.com.springecommerce.sales.cart.domain.status.CartStatus;
import br.com.springecommerce.sales.cart.domain.valueobject.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartTests {

    private UUID customerId;
    private Cart cart;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        cart = Cart.createFrom(customerId);
    }

    @Test
    void shouldCreateAnActiveCartWithEmptyItems() {
        // Then
        assertNotNull(cart.getId());
        assertEquals(customerId, cart.getCustomerId());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(CartStatus.ACTIVE, cart.getStatus());
    }

    @Test
    void shouldAddProductToCart() {
        // Given
        UUID productId = UUID.randomUUID();
        int quantity = 2;

        // When
        cart.addItem(productId, quantity);

        // Then
        assertEquals(1, cart.getItems().size());

        CartItem item = cart.getItems().iterator().next();

        assertEquals(productId, item.getProductId());
        assertEquals(quantity, item.getQuantity());
    }

    @Test
    void shouldAddMultipleProductsToCart() {
        // Given
        UUID firstProductId = UUID.randomUUID();
        UUID secondProductId = UUID.randomUUID();

        // When
        cart.addItem(firstProductId, 2);
        cart.addItem(secondProductId, 3);

        // Then
        assertEquals(2, cart.getItems().size());
    }

    @Test
    void shouldUpdateExistingProductQuantity() {
        // Given
        UUID productId = UUID.randomUUID();

        cart.addItem(productId, 2);

        // When
        cart.addItem(productId, 3);

        // Then
        assertEquals(1, cart.getItems().size());

        CartItem item = cart.getItems().iterator().next();

        assertEquals(productId, item.getProductId());
        assertEquals(3, item.getQuantity());
    }

    @Test
    void shouldRemoveProductFromCart() {
        // Given
        UUID firstProductId = UUID.randomUUID();
        UUID secondProductId = UUID.randomUUID();

        cart.addItem(firstProductId, 2);
        cart.addItem(secondProductId, 3);

        // When
        cart.removeItem(firstProductId);

        // Then
        assertEquals(1, cart.getItems().size());
        assertEquals(
                secondProductId,
                cart.getItems().iterator().next().getProductId()
        );
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
    void shouldRemoveLastProductFromCart() {
        // Given
        UUID productId = UUID.randomUUID();

        cart.addItem(productId, 2);

        // When
        cart.removeItem(productId);

        // Then
        assertTrue(cart.getItems().isEmpty());
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
