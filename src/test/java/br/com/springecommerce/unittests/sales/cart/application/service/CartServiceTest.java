package br.com.springecommerce.unittests.sales.cart.application.service;

import br.com.springecommerce.catalog.product.application.service.ProductService;
import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;
import br.com.springecommerce.catalog.product.application.snapshot.ProductStockSnapshot;
import br.com.springecommerce.catalog.product.domain.exception.ProductErrors;
import br.com.springecommerce.inventory.stock.domain.exception.InsufficientStockException;
import br.com.springecommerce.sales.cart.application.dto.CartAddProductRequestDto;
import br.com.springecommerce.sales.cart.application.dto.CartItemResponseDto;
import br.com.springecommerce.sales.cart.application.dto.CartResponseDto;
import br.com.springecommerce.sales.cart.application.service.CartService;
import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import br.com.springecommerce.sales.cart.domain.exception.CartItemNotFoundException;
import br.com.springecommerce.sales.cart.domain.model.Cart;
import br.com.springecommerce.sales.cart.domain.status.CartStatus;
import br.com.springecommerce.sales.cart.domain.valueobject.CartItem;
import br.com.springecommerce.sales.cart.infrastructure.persistence.repository.CartRepository;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository repository;

    @Mock
    private ProductService productService;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(
                repository,
                productService
        );
    }

    @Test
    void shouldReturnTheExistingActiveCartWithItsItems() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";

        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

        Cart cart = Cart.createFrom(customerId);

        cart.addItem(
                productId,
                productTitle,
                productImage,
                unitPrice,
                quantity
        );

        CartItemSnapshot cartItemSnapshot = new CartItemSnapshot(
                productId,
                productTitle,
                productImage,
                unitPrice,
                quantity,
                totalPrice
        );

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

        when(repository.findCartItems(cart.getId()))
                .thenReturn(List.of(cartItemSnapshot));

// When
        CartResponseDto response = cartService.getCart(customerId);

// Then
        assertEquals(cart.getId(), response.id());
        assertEquals(customerId, response.customerId());
        assertEquals(cart.getSubtotalAmount(), response.subTotalAmount());
        assertEquals(
                List.of(
                        new CartItemResponseDto(
                                cartItemSnapshot
                        )
                ),
                response.items()
        );

        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );
        verify(repository).findCartItems(cart.getId());
        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnAnInMemoryCartWhenNoActiveCartExists() {
        // Given
        UUID customerId = UUID.randomUUID();

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // When
        CartResponseDto response = cartService.getCart(customerId);

        // Then
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(customerId, response.customerId());
        assertEquals(BigDecimal.ZERO, response.subTotalAmount());
        assertTrue(response.items().isEmpty());

        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );
        verify(repository, never()).findCartItems(any());
    }

    @Test
    void shouldReturnAnInMemoryCartWhenOnlyAConvertedCartExists() {
        // Given
        UUID customerId = UUID.randomUUID();

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // When
        CartResponseDto response = cartService.getCart(customerId);

        // Then
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(customerId, response.customerId());
        assertEquals(BigDecimal.ZERO, response.subTotalAmount());
        assertTrue(response.items().isEmpty());

        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );
        verify(repository, never()).findCartItems(any());
    }


    @Test
    void shouldCreateAnActiveCartAndAddTheFirstProductWhenNoActiveCartExists() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";

        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;

        ProductSnapshot productSnapshot = new ProductSnapshot(
                productId,
                productTitle,
                productImage,
                unitPrice,
                null,
                true
        );

        ProductStockSnapshot productStockSnapshot = new ProductStockSnapshot(
                productSnapshot, quantity
        );

        Cart savedCart = Cart.createFrom(customerId);

        CartAddProductRequestDto dto =
                new CartAddProductRequestDto(productId, quantity);

        when(productService.getAvailableProduct(productId))
                .thenReturn(productStockSnapshot);

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.empty());

        when(repository.save(any(Cart.class)))
                .thenReturn(savedCart);

        // When
        cartService.addProduct(customerId, dto);

        // Then
        verify(productService).getAvailableProduct(productId);
        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );
        verify(repository).save(any(Cart.class));

        assertEquals(customerId, savedCart.getCustomerId());
        assertEquals(
                unitPrice.multiply(BigDecimal.valueOf(quantity)),
                savedCart.getSubtotalAmount()
        );
    }


    @Test
    void shouldAddAProductToTheExistingActiveCart() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";

        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;

        ProductSnapshot productSnapshot = new ProductSnapshot(
                productId,
                productTitle,
                productImage,
                unitPrice,
                null,
                true
        );

        ProductStockSnapshot productStockSnapshot = new ProductStockSnapshot(
                productSnapshot, quantity
        );

        Cart cart = Cart.createFrom(customerId);

        CartAddProductRequestDto dto = new CartAddProductRequestDto(
                productId,
                quantity
        );

        when(productService.getAvailableProduct(productId))
                .thenReturn(productStockSnapshot);

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

        // When
        cartService.addProduct(customerId, dto);

        // Then
        verify(productService).getAvailableProduct(productId);
        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );
        verify(repository, never()).save(any(Cart.class));

        assertEquals(
                unitPrice.multiply(BigDecimal.valueOf(quantity)),
                cart.getSubtotalAmount()
        );
        assertEquals(1, cart.getItems().size());
    }

    @Test
    void shouldUpdateQuantityAndUnitPriceWhenTheProductIsAlreadyInTheCart() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal unitPrice = new BigDecimal("100.00");
        int existingQuantity = 2;
        int newQuantity = 3;

        int availableStock = 10;

        CartAddProductRequestDto dto = new CartAddProductRequestDto(
                productId,
                newQuantity
        );

        ProductSnapshot productSnapshot = new ProductSnapshot(
                productId,
                productTitle,
                productImage,
                unitPrice,
                null,
                true
        );

        ProductStockSnapshot productStockSnapshot = new ProductStockSnapshot(
                productSnapshot, availableStock
        );

        Cart cart = Cart.createFrom(customerId);
        cart.addItem(
                productId,
                productTitle,
                productImage,
                unitPrice,
                existingQuantity
        );

        CartItem existingItem = cart.getItems().iterator().next();

        when(productService.getAvailableProduct(productId))
                .thenReturn(productStockSnapshot);

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

        // When
        cartService.addProduct(customerId, dto);

        // Then
        verify(productService).getAvailableProduct(productId);
        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );
        verify(repository, never()).save(any(Cart.class));

        assertEquals(1, cart.getItems().size());
        assertEquals(newQuantity, existingItem.getQuantity());
        assertEquals(unitPrice, existingItem.getUnitPrice());
        assertEquals(
                unitPrice.multiply(BigDecimal.valueOf(newQuantity)),
                cart.getSubtotalAmount()
        );
    }



    @Test
    void shouldFailToAddProductWhenRequestedQuantityExceedsAvailableStock() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";

        BigDecimal unitPrice = new BigDecimal("100.00");

        int requestedQuantity = 11;
        int availableStock = 10;

        ProductSnapshot productSnapshot = new ProductSnapshot(
                productId,
                productTitle,
                productImage,
                unitPrice,
                null,
                true
        );

        ProductStockSnapshot productStockSnapshot = new ProductStockSnapshot(
                productSnapshot, availableStock
        );

        CartAddProductRequestDto dto = new CartAddProductRequestDto(
                productId,
                requestedQuantity
        );

        when(productService.getAvailableProduct(productId))
                .thenReturn(productStockSnapshot);

        // When / Then
        assertThrows(
                InsufficientStockException.class,
                () -> cartService.addProduct(customerId, dto)
        );

        verify(productService).getAvailableProduct(productId);
        verify(repository, never()).findByCustomerIdAndStatus(any(), any());
        verify(repository, never()).save(any(Cart.class));
    }


    @Test
    void shouldThrowWhenProductIsNotAvailable() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CartAddProductRequestDto dto = new CartAddProductRequestDto(
                productId,
                2
        );

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ProductErrors.Target.PRODUCT,
                ProductErrors.Code.PRODUCT_NOT_FOUND
        );

        when(productService.getAvailableProduct(productId))
                .thenThrow(exception);

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.addProduct(customerId, dto)
        );

        verify(productService).getAvailableProduct(productId);
        verifyNoInteractions(repository);
    }


    @Test
    void shouldRemoveProductSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";

        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;

        Cart cart = Cart.createFrom(customerId);
        cart.addItem(
                productId,
                productTitle,
                productImage,
                unitPrice,
                quantity
        );

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

        // When
        cartService.removeProduct(customerId, productId);

        // Then
        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );

        assertTrue(cart.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getSubtotalAmount());
    }


    @Test
    void shouldThrowWhenRemoveProductAndItIsNotInCart() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Cart cart = Cart.createFrom(customerId);

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

        // When / Then
        assertThrows(
                CartItemNotFoundException.class,
                () -> cartService.removeProduct(customerId, productId)
        );

        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );
    }


    @Test
    void shouldThrowWhenWhenRemoveProductAndActiveCartDoesNotExist() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.removeProduct(customerId, productId)
        );

        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );
    }


    @Test
    void shouldRemoveProductAndKeepOtherItems() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productToRemoveId = UUID.randomUUID();
        String productToRemoveTitle = "Barraca Camping";
        String productToRemoveImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal productToRemoveUnitPrice = new BigDecimal("100.00");
        Integer productToRemoveQuantity = 2;

        UUID otherProductId = UUID.randomUUID();
        String otherProductTitle = "Bota Trekking";
        String otherProductImage = "products/bota-trekking/ciyya0m68tobdemmgmkw";
        BigDecimal otherUnitPrice = new BigDecimal("50.00");
        Integer otherQuantity = 3;

        Cart cart = Cart.createFrom(customerId);
        cart.addItem(
                productToRemoveId,
                productToRemoveTitle,
                productToRemoveImage,
                productToRemoveUnitPrice,
                productToRemoveQuantity
        );

        cart.addItem(
                otherProductId,
                otherProductTitle,
                otherProductImage,
                otherUnitPrice,
                otherQuantity
        );

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

        // When
        cartService.removeProduct(customerId, productToRemoveId);

        // Then
        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );

        assertEquals(1, cart.getItems().size());
        assertTrue(
                cart.getItems().stream()
                        .anyMatch(item -> item.getProductId().equals(otherProductId))
        );
        assertEquals(
                otherUnitPrice.multiply(BigDecimal.valueOf(otherQuantity)),
                cart.getSubtotalAmount()
        );
    }


    @Test
    void shouldRemoveLastProductAndResetSubtotal() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;

        Cart cart = Cart.createFrom(customerId);
        cart.addItem(
                productId,
                productTitle,
                productImage,
                unitPrice,
                quantity
        );

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

        // When
        cartService.removeProduct(customerId, productId);

        // Then
        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );

        assertTrue(cart.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getSubtotalAmount());
    }
}