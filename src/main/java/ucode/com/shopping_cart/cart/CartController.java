// CartController.java
package ucode.com.shopping_cart.cart;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ucode.com.shopping_cart.cart.dto.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication auth) {
        return ResponseEntity.ok(cartService.getCart(auth));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication auth) {
        return ResponseEntity.ok(cartService.addToCart(request, auth));
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity,
            Authentication auth) {
        return ResponseEntity.ok(cartService.updateQuantity(itemId, quantity, auth));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long itemId,
            Authentication auth) {
        return ResponseEntity.ok(cartService.removeItem(itemId, auth));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication auth) {
        cartService.clearCart(auth);
        return ResponseEntity.ok("Cart cleared");
    }
}
