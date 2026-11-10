package br.com.springecommerce.unittests.sales.checkout.application.service;

import br.com.springecommerce.catalog.product.application.service.ProductService;
import br.com.springecommerce.catalog.product.domain.exception.ProductErrors;
import br.com.springecommerce.catalog.product.domain.exception.ProductUnavailableException;
import br.com.springecommerce.inventory.stock.application.movement.StockMovementItem;
import br.com.springecommerce.inventory.stock.application.service.StockCommandService;
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
import br.com.springecommerce.sales.order.application.dto.OrderItemResponseDto;
import br.com.springecommerce.sales.order.application.dto.OrderResponseDto;
import br.com.springecommerce.sales.order.application.service.OrderService;
import br.com.springecommerce.sales.order.domain.model.Order;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
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
    private StockCommandService stockService;

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
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        Cart cart = new Cart(customerId);
        cart.addItem(
                productId,
                productTitle,
                productImage,
                unitPrice,
                quantity
        );

        CartItemSnapshot item = new CartItemSnapshot(
                productId,
                productTitle,
                productTitle,
                unitPrice,
                quantity,
                subtotal
        );

        CartSnapshot snapshot = new CartSnapshot(cart, List.of(item));

        Order order = new Order(customerId, subtotal);

        OrderCreationData expectedOrderData = new OrderCreationData(
                customerId,
                subtotal,
                List.of(new OrderItemData(productId, unitPrice, quantity))
        );

        when(cartService.getCartSnapshot(customerId))
                .thenReturn(snapshot);

        when(orderService.create(expectedOrderData))
                .thenReturn(order);

        // When
        OrderResponseDto response = checkoutService.checkout(customerId);

        // Then
        assertEquals(order.getId(), response.id());
        assertEquals(order.getCustomerId(), response.customerId());
        assertEquals(order.getSubtotalAmount(), response.subTotalAmount());
        assertEquals(
                List.of(new OrderItemResponseDto(item)),
                response.items()
        );

        verify(cartService).getCartSnapshot(customerId);
        verify(productService).checkProductsAreActive(
                List.of(productId)
        );
        verify(stockService).reserveItems(
                List.of(new StockMovementItem(productId, quantity))
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
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        Cart cart = new Cart(customerId);
        cart.addItem(
                productId,
                productTitle,
                productImage,
                unitPrice,
                quantity
        );

        CartItemSnapshot item = new CartItemSnapshot(
                productId,
                productTitle,
                productTitle,
                unitPrice,
                quantity,
                subtotal
        );

        CartSnapshot snapshot = new CartSnapshot(cart, List.of(item));

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ProductErrors.Target.PRODUCT,
                ProductErrors.Code.PRODUCT_NOT_FOUND
        );

        List<UUID> productIds = snapshot.items()
                .stream()
                .map(CartItemSnapshot::productId)
                .toList();

        when(cartService.getCartSnapshot(customerId))
                .thenReturn(snapshot);

        doThrow(exception)
                .when(productService)
                .checkProductsAreActive(productIds);

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> checkoutService.checkout(customerId)
        );

        verify(cartService).getCartSnapshot(customerId);
        verifyNoInteractions(stockService, orderService);
    }

    @Test
    void shouldFailCheckoutWhenProductIsUnavailable() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        Cart cart = new Cart(customerId);
        cart.addItem(
                productId,
                productTitle,
                productImage,
                unitPrice,
                quantity
        );

        CartItemSnapshot item = new CartItemSnapshot(
                productId,
                productTitle,
                productTitle,
                unitPrice,
                quantity,
                subtotal
        );

        CartSnapshot snapshot = new CartSnapshot(cart, List.of(item));

        ProductUnavailableException exception = new ProductUnavailableException(
                ProductErrors.Target.PRODUCT,
                ProductErrors.Code.PRODUCT_UNAVAILABLE
        );

        List<UUID> productIds = snapshot.items()
                .stream()
                .map(CartItemSnapshot::productId)
                .toList();

        when(cartService.getCartSnapshot(customerId))
                .thenReturn(snapshot);

        doThrow(exception)
                .when(productService)
                .checkProductsAreActive(productIds);

        // When / Then
        assertThrows(
                ProductUnavailableException.class,
                () -> checkoutService.checkout(customerId)
        );

        verify(cartService).getCartSnapshot(customerId);
        verifyNoInteractions(orderService);
    }

    @Test
    void shouldFailCheckoutWhenStockIsNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        Cart cart = new Cart(customerId);
        cart.addItem(
                productId,
                productTitle,
                productImage,
                unitPrice,
                quantity
        );

        CartItemSnapshot item = new CartItemSnapshot(
                productId,
                productTitle,
                productTitle,
                unitPrice,
                quantity,
                subtotal
        );

        CartSnapshot snapshot = new CartSnapshot(cart, List.of(item));

        ResourceNotFoundException exception = new ResourceNotFoundException(
                StockErrors.Target.PRODUCT,
                StockErrors.Code.STOCK_NOT_FOUND
        );

        List<StockMovementItem> reservationItems = snapshot.items()
                .stream()
                .map(cartItem -> new StockMovementItem(
                        cartItem.productId(),
                        cartItem.quantity()
                ))
                .toList();

        when(cartService.getCartSnapshot(customerId))
                .thenReturn(snapshot);

        doThrow(exception)
                .when(stockService)
                .reserveItems(reservationItems);

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> checkoutService.checkout(customerId)
        );

        verify(cartService).getCartSnapshot(customerId);
        verifyNoInteractions(orderService);
    }

    @Test
    void shouldFailCheckoutWhenStockIsInsufficient() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productTitle = "Barraca Camping";
        String productImage = "https://res.cloudinary.com/sjag7gwa/image/upload/v1794235299/products/barraca-camping-para-3-pessoas/ciyya0m68tobdemmgmkw.webp";
        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        Cart cart = new Cart(customerId);
        cart.addItem(
                productId,
                productTitle,
                productImage,
                unitPrice,
                quantity
        );

        CartItemSnapshot item = new CartItemSnapshot(
                productId,
                productTitle,
                productTitle,
                unitPrice,
                quantity,
                subtotal
        );

        CartSnapshot snapshot = new CartSnapshot(cart, List.of(item));

        InsufficientStockException exception = new InsufficientStockException(
                StockErrors.Target.QUANTITY,
                StockErrors.Code.STOCK_QUANTITY_INSUFFICIENT
        );

        List<StockMovementItem> reservationItems = snapshot.items()
                .stream()
                .map(cartItem -> new StockMovementItem(
                        cartItem.productId(),
                        cartItem.quantity()
                ))
                .toList();

        when(cartService.getCartSnapshot(customerId))
                .thenReturn(snapshot);

        doThrow(exception)
                .when(stockService)
                .reserveItems(reservationItems);

        // When / Then
        assertThrows(
                InsufficientStockException.class,
                () -> checkoutService.checkout(customerId)
        );

        verify(cartService).getCartSnapshot(customerId);
        verifyNoInteractions(orderService);
    }
}