package ucode.com.shopping_cart.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ucode.com.shopping_cart.auth.dto.AuthResponse;
import ucode.com.shopping_cart.auth.dto.LoginRequest;
import ucode.com.shopping_cart.auth.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor  // Lombok for constructor injection
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // Add this to any controller temporarily
    @GetMapping("/test")
    public String test() {
        return "You're authenticated! " + SecurityContextHolder.getContext().getAuthentication().getName();
    }
}