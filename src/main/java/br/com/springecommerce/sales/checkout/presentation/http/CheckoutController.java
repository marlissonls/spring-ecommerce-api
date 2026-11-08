package br.com.springecommerce.sales.checkout.presentation.http;

import br.com.springecommerce.iam.security.principal.AuthenticatedUser;
import br.com.springecommerce.sales.checkout.application.service.CheckoutService;
import br.com.springecommerce.sales.checkout.presentation.docs.CheckoutControllerDocs;
import br.com.springecommerce.sales.order.application.dto.OrderResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutController implements CheckoutControllerDocs {
    private final CheckoutService service;

    public CheckoutController(CheckoutService service) {
        this.service = service;
    }

    @Override
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDto> checkout(
            @AuthenticationPrincipal AuthenticatedUser auth
    ) {
        OrderResponseDto response = service.checkout(auth.getCustomerId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
