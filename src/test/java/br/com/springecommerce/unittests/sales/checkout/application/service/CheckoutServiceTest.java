package br.com.springecommerce.unittests.sales.checkout.application.service;

import br.com.springecommerce.catalog.product.application.service.ProductService;
import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;
import br.com.springecommerce.catalog.product.domain.exception.ProductErrors;
import br.com.springecommerce.inventory.stock.application.dto.StockReservationRequest;
import br.com.springecommerce.inventory.stock.application.service.StockService;
import br.com.springecommerce.inventory.stock.domain.exception.InsufficientStockException;
import br.com.springecommerce.inventory.stock.domain.exception.StockErrors;
import br.com.springecommerce.sales.cart.application.service.CartService;
import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import br.com.springecommerce.sales.cart.application.snapshot.CartSnapshot;
import br.com.springecommerce.sales.cart.domain.exception.CartErrors;
import br.com.springecommerce.sales.cart.domain.exception.CartIsEmptyException;
import br.com.springecommerce.sales.cart.domain.model.Cart;
import br.com.springecommerce.sales.checkout.application.service.CheckoutService;
import br.com.springecommerce.sales.order.application.contract.OrderCreationData;
import br.com.springecommerce.sales.order.application.contract.OrderItemData;
import br.com.springecommerce.sales.order.application.dto.OrderResponseDto;
import br.com.springecommerce.sales.order.application.service.OrderService;
import br.com.springecommerce.sales.order.domain.model.Order;
import br.com.springecommerce.shared.exception.base.ConflictException;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import br.com.springecommerce.unittests.sales.support.ProductSnapshotTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private StockService stockService;

    @Mock
    private CartService cartService;

    @Mock
    private OrderService orderService;

    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        checkoutService = new CheckoutService(
                productService,
                stockService,
                cartService,
                orderService
        );
    }


    @Test
    void shouldCreateOrderWhenCheckoutIsValid() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        int quantity = 2;
        BigDecimal price = new BigDecimal("100.00");

        Cart cart = new Cart(customerId);
        cart.addItem(productId, quantity);

        CartItemSnapshot item = new CartItemSnapshot(
                productId,
                quantity
        );

        CartSnapshot snapshot = new CartSnapshot(
                cart,
                List.of(item)
        );

        ProductSnapshot product = ProductSnapshotTestDataBuilder.aProduct()
                .withId(productId)
                .withPrice(price)
                .build();

        OrderCreationData expectedOrderData = new OrderCreationData(
                customerId,
                List.of(
                        new OrderItemData(
                            productId,
                            product.title(),
                            product.productImage(),
                            product.price(),
                            quantity)
                )
        );

        Order order = Order.createFrom(expectedOrderData);

        when(cartService.getCartSnapshot(customerId))
                .thenReturn(snapshot);

        when(productService.findAvailableProducts(List.of(productId)))
                .thenReturn(List.of(product));

        when(orderService.create(expectedOrderData))
                .thenReturn(order);

        // When
        OrderResponseDto response = checkoutService.checkout(customerId);

        // Then
        assertEquals(order.getId(), response.id());
        assertEquals(order.getCustomerId(), response.customerId());
        assertEquals(order.getSubtotalAmount(), response.subTotalAmount());
        assertEquals(order.getTotalAmount(), response.totalAmount());

        verify(cartService).getCartSnapshot(customerId);

        verify(productService).findAvailableProducts(List.of(productId));

        verify(stockService).reserveItems(
                List.of(new StockReservationRequest(productId, quantity))
        );

        verify(orderService).create(expectedOrderData);

        verify(cartService).markAsConverted(cart);
    }


    @Test
    void shouldFailCheckoutWhenActiveCartIsNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                CartErrors.Target.CART,
                CartErrors.Code.CART_NOT_FOUND
        );

        when(cartService.getCartSnapshot(customerId))
                .thenThrow(exception);

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> checkoutService.checkout(customerId)
        );

        verify(cartService).getCartSnapshot(customerId);
        verifyNoInteractions(stockService, orderService);
    }

    @Test
    void shouldFailCheckoutWhenCartIsEmpty() {
        // Given
        UUID customerId = UUID.randomUUID();

        CartIsEmptyException exception = new CartIsEmptyException(
                CartErrors.Target.CART,
                CartErrors.Code.CART_IS_EMPTY
        );

        when(cartService.getCartSnapshot(customerId))
                .thenThrow(exception);

        // When / Then
        assertThrows(
                CartIsEmptyException.class,
                () -> checkoutService.checkout(customerId)
        );

        verify(cartService).getCartSnapshot(customerId);
        verifyNoInteractions(productService, stockService, orderService);
    }


    @Test
    void shouldFailCheckoutWhenProductIsNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        int quantity = 2;

        Cart cart = new Cart(customerId);
        cart.addItem(productId, quantity);

        CartItemSnapshot item = new CartItemSnapshot(
                productId,
                quantity
        );

        CartSnapshot snapshot = new CartSnapshot(
                cart,
                List.of(item)
        );

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ProductErrors.Target.PRODUCT,
                ProductErrors.Code.PRODUCT_NOT_FOUND
        );

        when(cartService.getCartSnapshot(customerId))
                .thenReturn(snapshot);

        when(productService.findAvailableProducts(List.of(productId)))
                .thenThrow(exception);

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> checkoutService.checkout(customerId)
        );

        verify(cartService).getCartSnapshot(customerId);

        verify(productService).findAvailableProducts(List.of(productId));

        verifyNoInteractions(stockService, orderService);

        verify(cartService, never()).markAsConverted(cart);
    }

    @Test
    void shouldFailCheckoutWhenProductIsUnavailable() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        int quantity = 2;

        Cart cart = new Cart(customerId);
        cart.addItem(productId, quantity);

        CartItemSnapshot item = new CartItemSnapshot(
                productId,
                quantity
        );

        CartSnapshot snapshot = new CartSnapshot(
                cart,
                List.of(item)
        );

        ConflictException exception = new ConflictException(
                ProductErrors.Target.PRODUCT,
                ProductErrors.Code.PRODUCT_UNAVAILABLE
        );

        when(cartService.getCartSnapshot(customerId))
                .thenReturn(snapshot);

        when(productService.findAvailableProducts(List.of(productId)))
                .thenThrow(exception);

        // When / Then
        assertThrows(
                ConflictException.class,
                () -> checkoutService.checkout(customerId)
        );

        verify(cartService).getCartSnapshot(customerId);

        verify(productService).findAvailableProducts(List.of(productId));

        verifyNoInteractions(stockService, orderService);

        verify(cartService, never()).markAsConverted(cart);
    }


    @Test
    void shouldFailCheckoutWhenStockIsNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        int quantity = 2;

        Cart cart = new Cart(customerId);
        cart.addItem(productId, quantity);

        CartItemSnapshot item = new CartItemSnapshot(
                productId,
                quantity
        );

        CartSnapshot snapshot = new CartSnapshot(
                cart,
                List.of(item)
        );

        ProductSnapshot product = ProductSnapshotTestDataBuilder.aProduct()
                .withId(productId)
                .build();

        List<ProductSnapshot> products = List.of(product);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                StockErrors.Target.PRODUCT_ID,
                StockErrors.Code.STOCK_NOT_FOUND
        );

        List<StockReservationRequest> reservationItems = List.of(
                new StockReservationRequest(productId, quantity)
        );

        when(cartService.getCartSnapshot(customerId))
                .thenReturn(snapshot);

        when(productService.findAvailableProducts(List.of(productId)))
                .thenReturn(products);

        doThrow(exception)
                .when(stockService)
                .reserveItems(reservationItems);

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> checkoutService.checkout(customerId)
        );

        verify(cartService).getCartSnapshot(customerId);

        verify(productService).findAvailableProducts(List.of(productId));

        verify(stockService).reserveItems(reservationItems);

        verifyNoInteractions(orderService);

        verify(cartService, never()).markAsConverted(cart);
    }



    @Test
    void shouldFailCheckoutWhenStockIsInsufficient() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        int quantity = 2;

        Cart cart = new Cart(customerId);
        cart.addItem(productId, quantity);

        CartItemSnapshot item = new CartItemSnapshot(
                productId,
                quantity
        );

        CartSnapshot snapshot = new CartSnapshot(
                cart,
                List.of(item)
        );

        ProductSnapshot product = ProductSnapshotTestDataBuilder.aProduct()
                .withId(productId)
                .build();

        List<StockReservationRequest> reservationItems = List.of(
                new StockReservationRequest(productId, quantity)
        );

        InsufficientStockException exception = new InsufficientStockException(
                StockErrors.Target.QUANTITY,
                StockErrors.Code.STOCK_QUANTITY_INSUFFICIENT
        );

        when(cartService.getCartSnapshot(customerId))
                .thenReturn(snapshot);

        when(productService.findAvailableProducts(List.of(productId)))
                .thenReturn(List.of(product));

        doThrow(exception)
                .when(stockService)
                .reserveItems(reservationItems);

        // When / Then
        assertThrows(
                InsufficientStockException.class,
                () -> checkoutService.checkout(customerId)
        );

        verify(cartService).getCartSnapshot(customerId);

        verify(productService).findAvailableProducts(List.of(productId));

        verify(stockService).reserveItems(reservationItems);

        verifyNoInteractions(orderService);

        verify(cartService, never()).markAsConverted(cart);
    }

}