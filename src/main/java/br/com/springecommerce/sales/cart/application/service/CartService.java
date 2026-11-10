package br.com.springecommerce.sales.cart.application.service;

import br.com.springecommerce.catalog.product.application.service.ProductService;
import br.com.springecommerce.catalog.product.application.snapshot.ProductStockSnapshot;
import br.com.springecommerce.inventory.stock.domain.exception.InsufficientStockException;
import br.com.springecommerce.inventory.stock.domain.exception.StockErrors;
import br.com.springecommerce.sales.cart.application.dto.CartAddProductRequestDto;
import br.com.springecommerce.sales.cart.application.dto.CartItemResponseDto;
import br.com.springecommerce.sales.cart.application.dto.CartResponseDto;
import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import br.com.springecommerce.sales.cart.application.snapshot.CartSnapshot;
import br.com.springecommerce.sales.cart.domain.exception.CartErrors;
import br.com.springecommerce.sales.cart.domain.exception.CartIsEmptyException;
import br.com.springecommerce.sales.cart.domain.model.Cart;
import br.com.springecommerce.sales.cart.domain.status.CartStatus;
import br.com.springecommerce.sales.cart.infrastructure.persistence.repository.CartRepository;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    private final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartRepository repository;
    private final ProductService productService;

    public CartService(
            CartRepository repository,
            ProductService productService
    ) {
        this.repository = repository;
        this.productService = productService;
    }

    public CartResponseDto getCart(UUID userId) {
        logger.debug("Fetching open cart for customerId={}", userId);

        Optional<Cart> cartOpt =  repository.findByCustomerIdAndStatus(userId, CartStatus.ACTIVE);

        Cart cart;
        List<CartItemResponseDto> items;

        if (cartOpt.isEmpty()) {
            cart = Cart.createFrom(userId);
            items = List.of();
        } else {
            cart = cartOpt.get();
            items = repository.findCartItems(cart.getId()).stream()
                    .map(CartItemResponseDto::new)
                    .toList();
        }

        return new CartResponseDto(cart, items);
    }

    @Transactional
    public void addProduct(UUID userId, CartAddProductRequestDto dto) {
        logger.debug("Adding product to cart. customerId={}, productId={}, quantity={}",
                userId, dto.productId(), dto.quantity());

        ProductStockSnapshot product = productService.getAvailableProduct(dto.productId());

        if (dto.quantity() > product.stock()) {
            throw new InsufficientStockException(
                    StockErrors.Target.QUANTITY,
                    StockErrors.Code.STOCK_QUANTITY_INSUFFICIENT
            );
        }

        Cart cart = repository.findByCustomerIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> repository.save(Cart.createFrom(userId)));

        cart.addItem(
                product.id(),
                product.title(),
                product.productImage(),
                product.effectivePrice(),
                dto.quantity()
        );

        logger.info("Product added to cart successfully. cartId={}, customerId={}, productId={}, quantity={}",
                cart.getId(), userId, dto.productId(), dto.quantity());
    }

    @Transactional
    public void removeProduct(UUID userId, UUID productId) {
        logger.debug("Removing product from cart. customerId={}, productId={}",
                userId, productId);

        Cart cart = findOpenCartOrThrow(userId);

        cart.removeItem(productId);

        logger.info("Product removed from cart successfully. cartId={}, customerId={}, productId={}",
                cart.getId(), userId, productId);
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
}