package br.com.springecommerce.unittests.sales.cart.application.service;

import br.com.springecommerce.catalog.product.application.service.ProductService;
import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;
import br.com.springecommerce.catalog.product.domain.exception.ProductErrors;
import br.com.springecommerce.inventory.stock.application.service.StockService;
import br.com.springecommerce.inventory.stock.domain.exception.InsufficientStockException;
import br.com.springecommerce.inventory.stock.domain.exception.StockErrors;
import br.com.springecommerce.sales.cart.application.dto.CartAddProductRequestDto;
import br.com.springecommerce.sales.cart.application.dto.CartItemResponseDto;
import br.com.springecommerce.sales.cart.application.dto.CartResponseDto;
import br.com.springecommerce.sales.cart.application.service.CartService;
import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import br.com.springecommerce.sales.cart.domain.exception.CartItemNotFoundException;
import br.com.springecommerce.sales.cart.domain.model.Cart;
import br.com.springecommerce.sales.cart.domain.service.CartCalculator;
import br.com.springecommerce.sales.cart.domain.status.CartItemAvailability;
import br.com.springecommerce.sales.cart.domain.status.CartStatus;
import br.com.springecommerce.sales.cart.domain.valueobject.CartItem;
import br.com.springecommerce.sales.cart.infrastructure.persistence.repository.CartRepository;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import br.com.springecommerce.unittests.sales.support.ProductSnapshotTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository repository;

    @Mock
    private CartCalculator cartCalculator;

    @Mock
    private ProductService productService;

    @Mock
    private StockService stockService;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(
                repository,
                cartCalculator,
                productService,
                stockService
        );
    }

    @Test
    void shouldReturnTheExistingActiveCartWithItsItems() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        int quantity = 2;

        Cart cart = Cart.createFrom(customerId);
        cart.addItem(productId, quantity);

        CartItemSnapshot cartItemSnapshot = new CartItemSnapshot(
                productId,
                quantity
        );

        ProductSnapshot product = ProductSnapshotTestDataBuilder.aProduct()
                .withId(productId)
                .build();

        BigDecimal itemTotal = new BigDecimal("200.00");
        BigDecimal subtotal = new BigDecimal("200.00");

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

        when(repository.findCartItems(cart.getId()))
                .thenReturn(List.of(cartItemSnapshot));

        when(productService.findByIdIn(List.of(productId)))
                .thenReturn(List.of(product));

        when(cartCalculator.calculateSubtotal(
                List.of(cartItemSnapshot),
                Map.of(productId, product)
        )).thenReturn(subtotal);

        when(cartCalculator.calculateItemTotal(cartItemSnapshot, product))
                .thenReturn(Optional.of(itemTotal));

        // When
        CartResponseDto response = cartService.getCart(customerId);

        // Then
        assertEquals(cart.getId(), response.id());
        assertEquals(customerId, response.customerId());
        assertEquals(subtotal, response.subTotalAmount());
        assertEquals(1, response.items().size());

        CartItemResponseDto item = response.items().iterator().next();

        assertEquals(productId, item.productId());
        assertEquals(product.title(), item.productTitle());
        assertEquals(product.effectivePrice(), item.price());
        assertEquals(quantity, item.quantity());
        assertEquals(itemTotal, item.totalPrice());
        assertEquals(CartItemAvailability.AVAILABLE, item.availability());

        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );
        verify(repository).findCartItems(cart.getId());
        verify(productService).findByIdIn(List.of(productId));
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
        verifyNoInteractions(productService);
    }

    @Test
    void shouldCreateAnActiveCartAndAddTheFirstProductWhenNoActiveCartExists() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        int quantity = 2;

        CartAddProductRequestDto dto = new CartAddProductRequestDto(
                productId,
                quantity
        );

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.empty());

        when(repository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        cartService.addProduct(customerId, dto);

        // Then
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        verify(repository).save(cartCaptor.capture());

        Cart capturedCart = cartCaptor.getValue();

        assertEquals(customerId, capturedCart.getCustomerId());
        assertEquals(CartStatus.ACTIVE, capturedCart.getStatus());
        assertEquals(1, capturedCart.getItems().size());

        CartItem item = capturedCart.getItems().iterator().next();

        assertEquals(productId, item.getProductId());
        assertEquals(quantity, item.getQuantity());

        verify(productService).validateActive(productId);
        verify(stockService).validateAvailableStock(productId, quantity);

        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );
    }

    @Test
    void shouldAddAProductToTheExistingActiveCart() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        int quantity = 2;

        Cart cart = Cart.createFrom(customerId);

        CartAddProductRequestDto dto = new CartAddProductRequestDto(
                productId,
                quantity
        );

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

        // When
        cartService.addProduct(customerId, dto);

        // Then
        verify(repository, never()).save(any(Cart.class));

        verify(productService).validateActive(productId);
        verify(stockService).validateAvailableStock(productId, quantity);

        assertEquals(1, cart.getItems().size());

        CartItem item = cart.getItems().iterator().next();

        assertEquals(productId, item.getProductId());
        assertEquals(quantity, item.getQuantity());
    }

    @Test
    void shouldUpdateExistingProductQuantity() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        int existingQuantity = 2;
        int newQuantity = 3;

        CartAddProductRequestDto dto = new CartAddProductRequestDto(
                productId,
                newQuantity
        );

        Cart cart = Cart.createFrom(customerId);
        cart.addItem(productId, existingQuantity);

        CartItem existingItem = cart.getItems().iterator().next();

        when(repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

        // When
        cartService.addProduct(customerId, dto);

        // Then
        verify(productService).validateActive(productId);
        verify(stockService).validateAvailableStock(productId, newQuantity);

        verify(repository).findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );

        verify(repository, never()).save(any(Cart.class));

        assertEquals(1, cart.getItems().size());
        assertEquals(newQuantity, existingItem.getQuantity());
    }



    @Test
    void shouldFailToAddProductWhenRequestedQuantityExceedsAvailableStock() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        int requestedQuantity = 11;

        CartAddProductRequestDto dto = new CartAddProductRequestDto(
                productId,
                requestedQuantity
        );

        doThrow(new InsufficientStockException(
                StockErrors.Target.STOCK,
                StockErrors.Code.STOCK_QUANTITY_INSUFFICIENT
        )).when(stockService).validateAvailableStock(
                productId,
                requestedQuantity
        );

        // When / Then
        assertThrows(
                InsufficientStockException.class,
                () -> cartService.addProduct(customerId, dto)
        );

        verify(productService).validateActive(productId);
        verify(stockService).validateAvailableStock(productId, requestedQuantity);

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

        doThrow(new ResourceNotFoundException(
                ProductErrors.Target.PRODUCT,
                ProductErrors.Code.PRODUCT_NOT_FOUND
        )).when(productService).validateActive(productId);

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.addProduct(customerId, dto)
        );

        verify(productService).validateActive(productId);
        verifyNoInteractions(stockService);
        verifyNoInteractions(repository);
    }


    @Test
    void shouldRemoveProductSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Cart cart = Cart.createFrom(customerId);
        cart.addItem(productId, 2);

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
    }


    @Test
    void shouldThrowWhenRemovingProductThatDoesNotExistInCart() {
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
        UUID otherProductId = UUID.randomUUID();

        Cart cart = Cart.createFrom(customerId);
        cart.addItem(productToRemoveId, 2);
        cart.addItem(otherProductId, 3);

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
        assertFalse(
                cart.getItems().stream()
                        .anyMatch(item -> item.getProductId().equals(productToRemoveId))
        );
    }
}