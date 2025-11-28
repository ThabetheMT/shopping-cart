// dto/CartResponse.java
package ucode.com.shopping_cart.cart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        int totalItems,
        BigDecimal totalPrice,
        LocalDateTime updatedAt
) {}

