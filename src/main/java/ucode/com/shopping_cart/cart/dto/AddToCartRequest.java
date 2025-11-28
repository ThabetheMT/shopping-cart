// dto/AddToCartRequest.java
package ucode.com.shopping_cart.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
        @NotNull Long productId,
        @NotNull String productName,
        @NotNull java.math.BigDecimal price,
        String imageUrl,
        @Min(1) int quantity
) {}