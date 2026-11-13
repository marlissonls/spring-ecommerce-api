package br.com.springecommerce.sales.cart.application.service;

import br.com.springecommerce.catalog.product.application.service.ProductService;
import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;
import br.com.springecommerce.inventory.stock.application.service.StockService;
import br.com.springecommerce.sales.cart.application.dto.CartAddProductRequestDto;
import br.com.springecommerce.sales.cart.application.dto.CartItemResponseDto;
import br.com.springecommerce.sales.cart.application.dto.CartResponseDto;
import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import br.com.springecommerce.sales.cart.application.snapshot.CartSnapshot;
import br.com.springecommerce.sales.cart.domain.exception.CartErrors;
import br.com.springecommerce.sales.cart.domain.exception.CartIsEmptyException;
import br.com.springecommerce.sales.cart.domain.model.Cart;
import br.com.springecommerce.sales.cart.domain.service.CartCalculator;
import br.com.springecommerce.sales.cart.domain.status.CartItemAvailability;
import br.com.springecommerce.sales.cart.domain.status.CartStatus;
import br.com.springecommerce.sales.cart.infrastructure.persistence.repository.CartRepository;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartRepository repository;

    private final CartCalculator cartCalculator;

    private final ProductService productService;

    private final StockService stockService;

    public CartService(
            CartRepository repository,
            CartCalculator cartCalculator,
            ProductService productService,
            StockService stockService
    ) {
        this.repository = repository;
        this.cartCalculator = cartCalculator;
        this.productService = productService;
        this.stockService = stockService;
    }

    public CartResponseDto getCart(UUID customerId) {
        logger.debug("Fetching open cart for customerId={}", customerId);

        Optional<Cart> cartOpt = repository.findByCustomerIdAndStatus(
                customerId,
                CartStatus.ACTIVE
        );

        if (cartOpt.isEmpty()) {
            Cart cart = Cart.createFrom(customerId);

            return new CartResponseDto(
                    cart,
                    BigDecimal.ZERO,
                    List.of()
            );
        }

        Cart cart = cartOpt.get();

        List<CartItemSnapshot> cartItemSnapshots =
                repository.findCartItems(cart.getId());

        if (cartItemSnapshots.isEmpty()) {
            return new CartResponseDto(
                    cart,
                    BigDecimal.ZERO,
                    List.of()
            );
        }

        List<UUID> productIds = cartItemSnapshots.stream()
                .map(CartItemSnapshot::productId)
                .distinct()
                .toList();

        List<ProductSnapshot> productSnapshots =
                productService.findByIdIn(productIds);

        Map<UUID, ProductSnapshot> productMap = productSnapshots.stream()
                .collect(Collectors.toMap(
                        ProductSnapshot::id,
                        Function.identity()
                ));

        BigDecimal subtotal = cartCalculator.calculateSubtotal(
                cartItemSnapshots,
                productMap
        );

        List<CartItemResponseDto> items = cartItemSnapshots.stream()
                .map(cartItem -> toCartItemResponse(
                        cartItem,
                        productMap.get(cartItem.productId())
                ))
                .toList();

        return new CartResponseDto(
                cart.getId(),
                cart.getCustomerId(),
                subtotal,
                items
        );
    }

    @Transactional
    public void addProduct(UUID customerId, CartAddProductRequestDto dto) {
        logger.debug("Adding product to cart. customerId={}, productId={}, quantity={}",
                customerId, dto.productId(), dto.quantity());

        productService.validateActive(dto.productId());

        stockService.validateAvailableStock(
                dto.productId(), dto.quantity()
        );

        Cart cart = repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseGet(() -> repository.save(Cart.createFrom(customerId)));

        cart.addItem(
                dto.productId(), dto.quantity()
        );

        logger.info("Product added to cart successfully. cartId={}, customerId={}, productId={}, quantity={}",
                cart.getId(), customerId, dto.productId(), dto.quantity());
    }

    @Transactional
    public void removeProduct(UUID customerId, UUID productId) {
        logger.debug("Removing product from cart. customerId={}, productId={}",
                customerId, productId);

        Cart cart = findOpenCartOrThrow(customerId);

        cart.removeItem(productId);

        logger.info("Product removed from cart successfully. cartId={}, customerId={}, productId={}",
                cart.getId(), customerId, productId);
    }

    public void markAsConverted(Cart cart) {
        cart.markAsConverted();
    }

    public CartSnapshot getCartSnapshot(UUID customerId) {
        Cart cart = findOpenCartOrThrow(customerId);

        List<CartItemSnapshot> items = repository.findCartItems(cart.getId());

        if (items.isEmpty()) {
            throw new CartIsEmptyException(
                    CartErrors.Target.CART,
                    CartErrors.Code.CART_IS_EMPTY
            );
        }

        return new CartSnapshot(cart, items);
    }

    private Cart findOpenCartOrThrow(UUID customerId) {
        return repository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CartErrors.Target.CART,
                        CartErrors.Code.CART_NOT_FOUND
                ));
    }

    private CartItemResponseDto toCartItemResponse(
            CartItemSnapshot cartItem,
            ProductSnapshot product
    ) {
        if (product == null) {
            return new CartItemResponseDto(
                    cartItem.productId(),
                    null,
                    null,
                    null,
                    cartItem.quantity(),
                    null,
                    CartItemAvailability.UNAVAILABLE
            );
        }

        boolean active = product.active();

        return new CartItemResponseDto(
                product.id(),
                product.title(),
                product.productImage(),
                product.effectivePrice(),
                cartItem.quantity(),
                cartCalculator.calculateItemTotal(cartItem, product).orElse(null),
                active
                        ? CartItemAvailability.AVAILABLE
                        : CartItemAvailability.INACTIVE
        );
    }
}