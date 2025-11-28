package ucode.com.shopping_cart.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long itemId,
        Long productId,
        String productName,
        String imageUrl,
        BigDecimal price,
        int quantity,
        BigDecimal subtotal
) {}
