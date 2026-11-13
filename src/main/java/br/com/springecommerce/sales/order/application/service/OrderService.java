package br.com.springecommerce.sales.order.application.service;

import br.com.springecommerce.customer.customer.application.service.CustomerService;
import br.com.springecommerce.inventory.stock.application.dto.StockReservationRequest;
import br.com.springecommerce.inventory.stock.application.service.StockService;
import br.com.springecommerce.sales.order.application.contract.OrderCreationData;
import br.com.springecommerce.sales.order.application.dto.OrderItemResponseDto;
import br.com.springecommerce.sales.order.application.dto.OrderResponseDto;
import br.com.springecommerce.sales.order.domain.model.Order;
import br.com.springecommerce.sales.order.domain.exception.OrderErrors;
import br.com.springecommerce.sales.order.infrastructure.persistence.repository.OrderRepository;
import br.com.springecommerce.sales.order.domain.valueobject.OrderItem;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import br.com.springecommerce.shared.pagination.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository repository;

    private final StockService stockService;

    public OrderService(
            OrderRepository repository,
            StockService stockService,
            CustomerService customerService
    ) {
        this.repository = repository;
        this.stockService = stockService;
    }

    public OrderResponseDto getOrder(UUID id, UUID customerId) {
        logger.debug("Fetching order for orderId={}, customerId={}", id, customerId);

        Order order = repository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        OrderErrors.Target.ID,
                        OrderErrors.Code.ORDER_NOT_FOUND
        ));

        List<OrderItemResponseDto> items = repository.findOrderItems(order.getId())
                .stream()
                .map(OrderItemResponseDto::new)
                .toList();

        return new OrderResponseDto(order, items);
    }

    public PageResponse<OrderResponseDto> getOrders(UUID customerId, Pageable pageable) {
        logger.debug("Fetching orders for customerId={}", customerId);

        Page<Order> page = repository.findAllByCustomerId(customerId, pageable);

        List<OrderResponseDto> orders = page
                .map(order -> new OrderResponseDto(order, null))
                .getContent();

        return new PageResponse<>(
                orders,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    public Order create(OrderCreationData data) {
        Order order = Order.createFrom(data);

        return repository.save(order);
    }

    @Transactional
    public void cancel(UUID id, UUID customerId) {
        logger.debug("Cancelling order. orderId={}, customerId={}", id, customerId);

        Order order = repository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        OrderErrors.Target.ID,
                        OrderErrors.Code.ORDER_NOT_FOUND
                ));

//        if (order.getStatus() == OrderStatus.PAID) { paymentService.refund(order.getId()); }

        order.markAsCancelled();

        stockService.releaseItems(buildReservationItems(order.getItems()));
    }

    private List<StockReservationRequest> buildReservationItems(Set<OrderItem> items) {
        return items
                .stream()
                .map(item -> new StockReservationRequest(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();
    }
}
