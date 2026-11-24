package br.com.springecommerce.sales.order.presentation.http;

import br.com.springecommerce.sales.order.application.dto.OrderResponseDto;
import br.com.springecommerce.sales.order.application.service.OrderService;
import br.com.springecommerce.iam.security.principal.AuthenticatedUser;
import br.com.springecommerce.sales.order.presentation.docs.OrderControllerDocs;
import br.com.springecommerce.sales.order.presentation.filter.OrderManagerFilter;
import br.com.springecommerce.sales.order.presentation.filter.OrderCustomerFilter;
import br.com.springecommerce.shared.pagination.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderControllerDocs {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDto> getOrder(
            @AuthenticationPrincipal AuthenticatedUser auth,
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok().body(service.getOrder(id, auth.getCustomerId()));
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<OrderResponseDto>> getCustomerViewOrders(
            @AuthenticationPrincipal AuthenticatedUser auth,
            @ModelAttribute OrderCustomerFilter filter,
            @PageableDefault(
                    size = 12,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok().body(
                service.getCustomerViewOrders(
                        auth.getCustomerId(),
                        filter,
                        pageable
                )
        );
    }

    @Override
    @GetMapping(value = "/manager", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PageResponse<OrderResponseDto>> getManagerViewOrders(
            @ModelAttribute OrderManagerFilter filter,
            @PageableDefault(
                    size = 12,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok().body(
                service.getManagerViewOrders(filter, pageable)
        );
    }

    @Override
    @PutMapping(path = "/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal AuthenticatedUser auth,
            @PathVariable("id") UUID id
    ) {
        service.cancel(id, auth.getCustomerId());
        return ResponseEntity.noContent().build();
    }
}
