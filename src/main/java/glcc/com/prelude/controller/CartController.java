package glcc.com.prelude.controller;

import glcc.com.prelude.dto.request.UpdateItemRequest;
import glcc.com.prelude.dto.response.CartResponse;
import glcc.com.prelude.exception.CartItemNotFoundException;
import glcc.com.prelude.exception.CartNotFoundException;
import glcc.com.prelude.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import glcc.com.prelude.dto.response.CheckoutPreparation;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PatchMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable UUID userId,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateItemRequest request) {
        try {
            CartResponse response = cartService.updateItemQuantity(userId, itemId, request.quantity());
            return ResponseEntity.ok(response);
        } catch (CartNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (CartItemNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable UUID userId,
            @PathVariable UUID itemId) {
        try {
            cartService.removeItem(userId, itemId);
            return ResponseEntity.noContent().build();
        } catch (CartNotFoundException | CartItemNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.noContent().build();
        } catch (CartNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<CheckoutPreparation> checkout(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.prepareCheckout(userId));
    }
}