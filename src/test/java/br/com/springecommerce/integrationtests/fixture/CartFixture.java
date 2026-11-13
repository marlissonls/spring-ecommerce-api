package br.com.springecommerce.integrationtests.fixture;

import br.com.springecommerce.catalog.product.domain.model.Product;
import br.com.springecommerce.customer.customer.domain.model.Customer;
import br.com.springecommerce.sales.cart.domain.model.Cart;
import br.com.springecommerce.sales.cart.domain.status.CartStatus;
import br.com.springecommerce.sales.cart.infrastructure.persistence.repository.CartRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CartFixture {

    private final CartRepository repository;

    public CartFixture(CartRepository repository) {
        this.repository = repository;
    }

    public Cart create(Customer customer) {
        return create(customer, CartStatus.ACTIVE);
    }

    public Cart create(Customer customer, CartStatus status) {
        Cart cart = new Cart(customer.getId());
        cart.setStatus(status);

        return repository.save(cart);
    }

    @Transactional
    public void addProduct(Cart cart, Product product, Integer quantity) {
        Cart managedCart = repository.findById(cart.getId())
                .orElseThrow();

        managedCart.addItem(
                product.getId(),
                quantity
        );
    }
}
