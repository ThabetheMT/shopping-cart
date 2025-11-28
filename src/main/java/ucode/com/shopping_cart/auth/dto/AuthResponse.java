package ucode.com.shopping_cart.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String message,
        Long userId,
        String fullName,
        String email,
        String role
) {}
