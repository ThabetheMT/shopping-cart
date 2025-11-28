package ucode.com.shopping_cart.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
