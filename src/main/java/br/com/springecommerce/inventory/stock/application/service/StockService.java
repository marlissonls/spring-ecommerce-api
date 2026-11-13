package br.com.springecommerce.inventory.stock.application.service;

import br.com.springecommerce.inventory.stock.application.dto.StockAvailableQuantityUpdateDto;
import br.com.springecommerce.inventory.stock.application.dto.StockCreateDto;
import br.com.springecommerce.inventory.stock.application.dto.StockMinimumQuantityUpdateDto;
import br.com.springecommerce.inventory.stock.application.dto.StockResponseDto;
import br.com.springecommerce.inventory.stock.domain.model.Stock;
import br.com.springecommerce.inventory.stock.application.dto.StockReservationRequest;
import br.com.springecommerce.inventory.stock.domain.exception.StockErrors;
import br.com.springecommerce.inventory.stock.infrastructure.persistence.StockRepository;
import br.com.springecommerce.inventory.stock.domain.exception.InsufficientStockException;
import br.com.springecommerce.inventory.stock.infrastructure.specification.StockSpecificationFactory;
import br.com.springecommerce.inventory.stock.presentation.filter.StockManagerFilter;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import br.com.springecommerce.shared.pagination.PageResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final StockRepository repository;

    public StockService(StockRepository repository) {
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

    public Optional<Stock> findByProductId(UUID productId) {
        return repository.findByProductId(productId);
    }

    public List<Stock> findByProductIdIn(List<UUID> ids) {
        return repository.findByProductIdIn(ids);
    }

    public void validateAvailableStock(UUID productId, Integer requestedQuantity) {
        Stock stock = repository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        StockErrors.Target.PRODUCT_ID,
                        StockErrors.Code.STOCK_NOT_FOUND));

        stock.validateAvailableStock(requestedQuantity);
    }

    @Transactional
    public StockResponseDto create(StockCreateDto dto) {
        logger.debug("Creating stock. productId={}", dto.productId());

        Stock stock = Stock.createFrom(
                dto.productId(),
                dto.availableQuantity(),
                dto.minimumQuantity()
        );

        Stock saved = repository.save(stock);

        logger.info("Stock created successfully. id={}, productId={}", saved.getId(), saved.getProductId());

        return new StockResponseDto(saved);
    }

    @Transactional
    public void updateMinimumQuantity(UUID id, StockMinimumQuantityUpdateDto dto) {
        logger.debug("Updating stock minimum quantity. id={}, new minimum quantity={}", id, dto.newMinimumQuantity());

        int updated = repository.updateMinimumQuantity(id, dto.newMinimumQuantity());

        if (updated == 0) {
            throw new ResourceNotFoundException(StockErrors.Target.STOCK, StockErrors.Code.STOCK_NOT_FOUND);
        }

        logger.info("Stock minimum quantity successfully updated. id={}, current minimum quantity={}", id, dto.newMinimumQuantity());
    }

    @Transactional
    public void increase(UUID id, StockAvailableQuantityUpdateDto dto) {
        logger.debug("Increasing stock quantity. id={}, quantity={}", id, dto.amount());

        int updated = repository.increase(id, dto.amount());

        if (updated == 0) {
            throw new ResourceNotFoundException(StockErrors.Target.STOCK, StockErrors.Code.STOCK_NOT_FOUND);
        }

        logger.info("Stock quantity increased successfully. id={}, quantity={}", id, dto.amount());
    }

    @Transactional
    public void writeOff(UUID id, StockAvailableQuantityUpdateDto dto) {
        logger.debug("Writing off stock quantity. id={}, quantity={}", id, dto.amount());

        int updated = repository.writeOff(id, dto.amount());

        if (updated == 0) {
            throw new ResourceNotFoundException(StockErrors.Target.STOCK, StockErrors.Code.STOCK_NOT_FOUND);
        }

        logger.info("Stock quantity written off successfully. id={}, quantity={}", id, dto.amount());
    }

    @Transactional
    public void reserve(UUID id, StockAvailableQuantityUpdateDto dto) {
        logger.debug("Reserving stock quantity. id={}, quantity={}", id, dto.amount());

        int updated = repository.reserve(id, dto.amount());

        if (updated == 0) {
            throw new ResourceNotFoundException(StockErrors.Target.STOCK, StockErrors.Code.STOCK_NOT_FOUND);
        }

        logger.info("Stock quantity reserved successfully. id={}, quantity={}", id, dto.amount());
    }

    @Transactional
    public void release(UUID id, StockAvailableQuantityUpdateDto dto) {
        logger.debug("Releasing reserved stock quantity. id={}, quantity={}", id, dto.amount());

        int updated = repository.release(id, dto.amount());

        if (updated == 0) {
            throw new ResourceNotFoundException(StockErrors.Target.STOCK, StockErrors.Code.STOCK_NOT_FOUND);
        }

        logger.info("Reserved stock quantity released successfully. id={}, quantity={}", id, dto.amount());
    }

    @Transactional
    public void commit(UUID id, StockAvailableQuantityUpdateDto dto) {
        logger.debug("Committing reserved stock quantity. id={}, quantity={}", id, dto.amount());

        int updated = repository.commit(id, dto.amount());

        if (updated == 0) {
            throw new ResourceNotFoundException(StockErrors.Target.STOCK, StockErrors.Code.STOCK_NOT_FOUND);
        }

        logger.info("Reserved stock quantity committed successfully. id={}, quantity={}", id, dto.amount());
    }

    public void reserveItems(@Valid List<StockReservationRequest> items) {
        List<UUID> productIds = items
                .stream()
                .map(StockReservationRequest::productId)
                .toList();

        List<Stock> stocks = repository.findByProductIdIn(productIds);

        Map<UUID, Stock> stockMap = stocks.stream()
                .collect(Collectors.toMap(
                        Stock::getProductId,
                        Function.identity()
                ));

        for (StockReservationRequest movementItem : items) {
            Stock stock = stockMap.get(movementItem.productId());

            if (stock == null) {
                throw new ResourceNotFoundException(
                        StockErrors.Target.PRODUCT_ID,
                        StockErrors.Code.STOCK_NOT_FOUND
                );
            }

           if (stock.getAvailableQuantity() < movementItem.quantity()) {
               throw new InsufficientStockException(
                        StockErrors.Target.QUANTITY,
                        StockErrors.Code.STOCK_QUANTITY_INSUFFICIENT
               );
           }

           int updatedRows = repository.reserve(stock.getId(), movementItem.quantity());

           if (updatedRows == 0) {
               throw new InsufficientStockException(
                       StockErrors.Target.QUANTITY,
                       StockErrors.Code.STOCK_QUANTITY_INSUFFICIENT
               );
           }
        }
    }

    public void releaseItems(@Valid List<StockReservationRequest> items) {
        for (StockReservationRequest item: items) {
            Stock stock = repository.findByProductId(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            StockErrors.Target.PRODUCT_ID,
                            StockErrors.Code.STOCK_NOT_FOUND
                    ));

            if (stock.getReservedQuantity() < item.quantity()) {
                throw new InsufficientStockException(
                        StockErrors.Target.RESERVED_QUANTITY,
                        StockErrors.Code.STOCK_RESERVED_QUANTITY_INSUFFICIENT
                );
            }

            int updatedRows = repository.release(stock.getId(), item.quantity());

            if (updatedRows == 0) {
                throw new InsufficientStockException(
                        StockErrors.Target.RESERVED_QUANTITY,
                        StockErrors.Code.STOCK_RESERVED_QUANTITY_INSUFFICIENT
                );
            }
        }
    }

    private Stock findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Stock not found. id={}", id);
                    return new ResourceNotFoundException(StockErrors.Target.ID, StockErrors.Code.STOCK_NOT_FOUND);
                });
    }
}
