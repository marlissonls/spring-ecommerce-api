package br.com.springecommerce.integrationtests.fixture;

import br.com.springecommerce.inventory.stock.domain.model.Stock;
import br.com.springecommerce.inventory.stock.infrastructure.persistence.StockRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StockFixture {

    private final StockRepository repository;

    private UUID productId;
    private Integer availableQuantity = 100;
    private Integer minimumQuantity = 10;

    public StockFixture(StockRepository repository) {
        this.repository = repository;
    }

    public StockFixture withProduct(UUID productId) {
        this.productId = productId;
        return this;
    }

    public StockFixture withAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
        return this;
    }

    public StockFixture withMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
        return this;
    }

    public Stock create() {
        Stock stock = new Stock(
                productId,
                availableQuantity,
                minimumQuantity
        );

        return repository.save(stock);
    }

    public void updateAvailableQuantity(Stock stock, Integer stockAvailableQuantityBeforeCheckout) {
        stock.setAvailableQuantity(stockAvailableQuantityBeforeCheckout);
        repository.save(stock);
    }

    public void delete(Stock stock) {
        repository.delete(stock);
    }
}

