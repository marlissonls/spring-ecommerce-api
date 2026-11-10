package br.com.springecommerce.inventory.stock.application.service;

import br.com.springecommerce.inventory.stock.application.dto.StockResponseDto;
import br.com.springecommerce.inventory.stock.domain.model.Stock;
import br.com.springecommerce.inventory.stock.domain.exception.StockErrors;
import br.com.springecommerce.inventory.stock.presentation.filter.StockManagerFilter;
import br.com.springecommerce.inventory.stock.infrastructure.persistence.StockRepository;
import br.com.springecommerce.inventory.stock.infrastructure.specification.StockSpecificationFactory;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import br.com.springecommerce.shared.pagination.PageResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StockQueryService {

    private static final Logger logger = LoggerFactory.getLogger(StockQueryService.class);

    private final StockRepository repository;

    public StockQueryService(StockRepository repository) {
        this.repository = repository;
    }

    public PageResponse<StockResponseDto> findAll(
            StockManagerFilter filter,
            Pageable pageable
    ) {
        logger.debug("Searching stocks. filter={}, pageable={}", filter, pageable);

        Specification<Stock> spec = StockSpecificationFactory.managerView(filter);

        Page<StockResponseDto> page = repository.findAll(spec, pageable)
                .map(StockResponseDto::new);

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    public StockResponseDto findById(UUID id) {
        logger.debug("Loading stock. id={}", id);

        return new StockResponseDto(findEntityById(id));
    }

    public List<Stock> findByProductIdIn(List<UUID> ids) {
        return repository.findByProductIdIn(ids);
    }

    private Stock findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Stock not found. id={}", id);
                    return new ResourceNotFoundException(StockErrors.Target.ID, StockErrors.Code.STOCK_NOT_FOUND);
                });
    }

    public Integer findAvailableQuantityByProductId(UUID productId) {
        Stock stock = repository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    StockErrors.Target.PRODUCT,
                    StockErrors.Code.STOCK_NOT_FOUND));

        return stock.getAvailableQuantity();
    }
}
