package br.com.springecommerce.sales.checkout.application.service;

import br.com.springecommerce.catalog.product.application.service.ProductService;
import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;
import br.com.springecommerce.inventory.stock.application.dto.StockReservationRequest;
import br.com.springecommerce.inventory.stock.application.service.StockService;
import br.com.springecommerce.sales.cart.application.service.CartService;
import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import br.com.springecommerce.sales.cart.application.snapshot.CartSnapshot;
import br.com.springecommerce.sales.order.application.contract.OrderCreationData;
import br.com.springecommerce.sales.order.application.contract.OrderItemData;
import br.com.springecommerce.sales.order.application.dto.OrderItemResponseDto;
import br.com.springecommerce.sales.order.application.dto.OrderResponseDto;
import br.com.springecommerce.sales.order.domain.model.Order;
import br.com.springecommerce.sales.order.application.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CheckoutService {
    private final Logger logger = LoggerFactory.getLogger(CheckoutService.class);

    private final ProductService productService;
    private final StockService stockService;
    private final CartService cartService;
    private final OrderService orderService;

    public CheckoutService(
            ProductService productService,
            StockService stockService,
            CartService cartService,
            OrderService orderService
    ) {
        this.productService = productService;
        this.stockService = stockService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @Transactional
    public OrderResponseDto checkout(UUID customerId) {
        logger.debug("Checkout started. customerId={}", customerId);

        CartSnapshot snapshot = cartService.getCartSnapshot(customerId);

        List<ProductSnapshot> products = productService.findAvailableProducts(extractProductIds(snapshot));

        stockService.reserveItems(buildReservationItems(snapshot.items()));

        Order order = orderService.create(
                buildOrderCreationData(snapshot, products)
        );

        cartService.markAsConverted(snapshot.cart());

        logger.info(
                "Checkout completed. customerId={}, orderId={}, items={}, total={}",
                customerId, order.getId(), snapshot.items().size(), order.getTotalAmount()
        );

        return new OrderResponseDto(order, buildOrderItemsResponse(snapshot, products));
    }

    private List<UUID> extractProductIds(CartSnapshot snapshot) {
        return snapshot.items()
                .stream()
                .map(CartItemSnapshot::productId)
                .toList();
    }

    private List<StockReservationRequest> buildReservationItems(List<CartItemSnapshot> items) {
        return items
                .stream()
                .map(item -> new StockReservationRequest(
                        item.productId(),
                        item.quantity()
                ))
                .toList();
    }

    private OrderCreationData buildOrderCreationData(
            CartSnapshot snapshot,
            List<ProductSnapshot> products
    ) {
        Map<UUID, ProductSnapshot> productMap = products.stream()
                .collect(Collectors.toMap(
                        ProductSnapshot::id,
                        Function.identity()
                ));

        return new OrderCreationData(
                snapshot.cart().getCustomerId(),
                snapshot.items().stream()
                        .map(item -> {
                            ProductSnapshot product = productMap.get(item.productId());

                            return new OrderItemData(
                                    item.productId(),
                                    product.title(),
                                    product.productImage(),
                                    product.effectivePrice(),
                                    item.quantity()
                            );
                        })
                        .toList()
        );
    }

    private List<OrderItemResponseDto> buildOrderItemsResponse(
            CartSnapshot snapshot,
            List<ProductSnapshot> products
    ) {
        Map<UUID, ProductSnapshot> productMap = products.stream()
                .collect(Collectors.toMap(
                        ProductSnapshot::id,
                        Function.identity()
                ));

        return snapshot.items()
                .stream()
                .map(item -> {
                    ProductSnapshot product = productMap.get(item.productId());

                    return new OrderItemResponseDto(
                            item.productId(),
                            product.title(),
                            product.productImage(),
                            product.effectivePrice(),
                            item.quantity(),
                            product.effectivePrice().multiply(BigDecimal.valueOf(item.quantity()))
                    );
                })
                .toList();
    }
}
