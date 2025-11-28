// src/test/java/ucode/com/shopping_cart/cart/CartServiceTest.java
package ucode.com.shopping_cart.cart;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import ucode.com.shopping_cart.cart.dto.AddToCartRequest;
import ucode.com.shopping_cart.cart.dto.CartResponse;
import ucode.com.shopping_cart.user.Role;
import ucode.com.shopping_cart.user.User;
import ucode.com.shopping_cart.user.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private UserRepository userRepository;
    @Mock private Authentication authentication;

    @InjectMocks private CartService cartService;

    private User testUser;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .customerId(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.CUSTOMER)
                .build();

        testCart = Cart.builder()
                .id(100L)
                .user(testUser)
                .build();

        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("getCart() - creates new cart when none exists")
    void getCart_createsNewCartWhenNoneExists() {
        when(cartRepository.findByUserCustomerId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        CartResponse response = cartService.getCart(authentication);

        assertNotNull(response);
        assertEquals(0, response.totalItems());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("addToCart() - adds new item to empty cart")
    void addToCart_addsNewItem() {
        when(cartRepository.findByUserCustomerId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        AddToCartRequest request = new AddToCartRequest(
                99L, "iPhone 15", new BigDecimal("999.99"), "img.jpg", 1
        );

        CartResponse response = cartService.addToCart(request, authentication);

        assertEquals(1, response.totalItems());
        assertEquals(new BigDecimal("999.99"), response.totalPrice());
        assertEquals("iPhone 15", response.items().get(0).productName());
        verify(cartRepository).save(testCart);
    }

    @Test
    @DisplayName("addToCart() - increases quantity of existing item")
    void addToCart_increasesQuantityOfExistingItem() {
        CartItem existingItem = CartItem.builder()
                .id(50L)
                .productId(99L)
                .productName("iPhone 15")
                .price(new BigDecimal("999.99"))
                .quantity(2)
                .cart(testCart)
                .build();
        testCart.getItems().add(existingItem);

        when(cartRepository.findByUserCustomerId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        AddToCartRequest request = new AddToCartRequest(
                99L, "iPhone 15", new BigDecimal("999.99"), null, 3
        );

        CartResponse response = cartService.addToCart(request, authentication);

        assertEquals(1, response.totalItems());
        assertEquals(5, response.items().get(0).quantity()); // 2 + 3
        assertEquals(new BigDecimal("4999.95"), response.totalPrice());
    }

    @Test
    @DisplayName("updateQuantity() - updates item quantity")
    void updateQuantity_updatesCorrectly() {
        CartItem item = CartItem.builder()
                .id(77L)
                .productId(10L)
                .price(new BigDecimal("50.00"))
                .quantity(1)
                .cart(testCart)
                .build();
        testCart.getItems().add(item);

        when(cartRepository.findByUserCustomerId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartResponse response = cartService.updateQuantity(77L, 4, authentication);

        assertEquals(4, response.items().get(0).quantity());
        assertEquals(new BigDecimal("200.00"), response.totalPrice());
    }

    @Test
    @DisplayName("updateQuantity() - removes item when quantity <= 0")
    void updateQuantity_removesItemWhenZeroOrNegative() {
        CartItem item = CartItem.builder()
                .id(88L)
                .productId(20L)
                .price(new BigDecimal("100"))
                .quantity(3)
                .cart(testCart)
                .build();
        testCart.getItems().add(item);

        when(cartRepository.findByUserCustomerId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartResponse response = cartService.updateQuantity(88L, 0, authentication);

        assertEquals(0, response.totalItems());
        assertFalse(testCart.getItems().contains(item));
    }

    @Test
    @DisplayName("removeItem() - removes correct item")
    void removeItem_removesCorrectItem() {
        CartItem item = CartItem.builder().id(99L).cart(testCart).build();
        testCart.getItems().add(item);

        when(cartRepository.findByUserCustomerId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartResponse response = cartService.removeItem(99L, authentication);

        assertEquals(0, response.totalItems());
        verify(cartRepository).save(testCart);
    }

    @Test
    @DisplayName("clearCart() - empties all items")
    void clearCart_emptiesCart() {
        testCart.getItems().add(CartItem.builder().build());
        testCart.getItems().add(CartItem.builder().build());

        when(cartRepository.findByUserCustomerId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        cartService.clearCart(authentication);

        assertEquals(0, testCart.getItems().size());
        verify(cartRepository).save(testCart);
    }

    @Test
    @DisplayName("getCart() - returns existing cart with correct total")
    void getCart_returnsExistingCartWithCorrectTotal() {
        CartItem item = CartItem.builder()
                .price(new BigDecimal("100"))
                .quantity(2)
                .build();
        testCart.getItems().add(item);

        when(cartRepository.findByUserCustomerId(1L)).thenReturn(Optional.of(testCart));

        CartResponse response = cartService.getCart(authentication);

        assertEquals(1, response.totalItems());
        assertEquals(new BigDecimal("200"), response.totalPrice());
    }
}