// CartService.java
package ucode.com.shopping_cart.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucode.com.shopping_cart.cart.dto.*;
import ucode.com.shopping_cart.user.User;
import ucode.com.shopping_cart.user.UserRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartResponse getCart(Authentication auth) {
        User user = getCurrentUser(auth);
        Cart cart = cartRepository.findByUserCustomerId(user.getCustomerId())
                .orElseGet(() -> createCartForUser(user));

        return mapToCartResponse(cart);
    }

    public CartResponse addToCart(AddToCartRequest request, Authentication auth) {
        User user = getCurrentUser(auth);
        Cart cart = cartRepository.findByUserCustomerId(user.getCustomerId())
                .orElseGet(() -> createCartForUser(user));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.quantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.productId())
                    .productName(request.productName())
                    .price(request.price())
                    .imageUrl(request.imageUrl())
                    .quantity(request.quantity())
                    .build();
            cart.addItem(newItem);
        }

        return mapToCartResponse(cartRepository.save(cart));
    }

    public CartResponse updateQuantity(Long itemId, int quantity, Authentication auth) {
        Cart cart = getCurrentUserCart(auth);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (quantity <= 0) {
            cart.removeItem(item);
        } else {
            item.setQuantity(quantity);
        }

        return mapToCartResponse(cartRepository.save(cart));
    }

    public CartResponse removeItem(Long itemId, Authentication auth) {
        Cart cart = getCurrentUserCart(auth);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        return mapToCartResponse(cartRepository.save(cart));
    }

    public void clearCart(Authentication auth) {
        Cart cart = getCurrentUserCart(auth);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // Helpers
    private User getCurrentUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Cart getCurrentUserCart(Authentication auth) {
        return cartRepository.findByUserCustomerId(getCurrentUser(auth).getCustomerId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    private Cart createCartForUser(User user) {
        Cart cart = Cart.builder().user(user).build();
        return cartRepository.save(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        var items = cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getImageUrl(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                )).toList();

        return new CartResponse(
                cart.getId(),
                items,
                items.size(),
                cart.getTotal(),
                cart.getUpdatedAt()
        );
    }
}
