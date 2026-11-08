package br.com.springecommerce.sales.cart.presentation.http;

import br.com.springecommerce.iam.security.principal.AuthenticatedUser;
import br.com.springecommerce.sales.cart.application.dto.CartAddProductRequestDto;
import br.com.springecommerce.sales.cart.application.dto.CartResponseDto;
import br.com.springecommerce.sales.cart.application.service.CartService;
import br.com.springecommerce.sales.cart.presentation.docs.CartControllerDocs;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController implements CartControllerDocs {
    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CartResponseDto> getCart(
            @AuthenticationPrincipal AuthenticatedUser auth
    ) {
        return ResponseEntity.ok(service.getCart(auth.getCustomerId()));
    }

    @Override
    @PostMapping(value = "/items", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addProduct(
            @AuthenticationPrincipal AuthenticatedUser auth,
            @RequestBody @Valid CartAddProductRequestDto dto
    ) {
        service.addProduct(auth.getCustomerId(), dto);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/items/product/{productId}")
    public ResponseEntity<Void> removeProduct(
            @AuthenticationPrincipal AuthenticatedUser auth,
            @PathVariable("productId") UUID productId
    ) {
        service.removeProduct(auth.getCustomerId(), productId);

        return ResponseEntity.noContent().build();
    }
}
