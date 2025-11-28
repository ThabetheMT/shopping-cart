package ucode.com.shopping_cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ucode.com.shopping_cart.auth.AuthService;
import ucode.com.shopping_cart.auth.dto.AuthResponse;
import ucode.com.shopping_cart.auth.dto.LoginRequest;
import ucode.com.shopping_cart.auth.dto.RegisterRequest;
import ucode.com.shopping_cart.common.custom.CustomUserDetailsService;
import ucode.com.shopping_cart.common.jwt.JwtUtil;
import ucode.com.shopping_cart.user.Role;
import ucode.com.shopping_cart.user.User;
import ucode.com.shopping_cart.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer register")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password")
                .phone("1234567890")
                .build();

        user = User.builder()
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .email(registerRequest.email())
              //  .password(bCryptPasswordEncoder.encode(registerRequest.password()))
                .phone(registerRequest.phone())
                .role(Role.CUSTOMER)
                .build();

        loginRequest = LoginRequest.builder()
                .email("john@example.com")
                .password("password")
                .build();
    }

    @Nested
    class RegisterNewUser {

        @Test
        @DisplayName("Sould register a new user successfully")
        void ShouldRegisterUser() {
            //give
            when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
            when(userRepository.save(user)).thenReturn(user);
            //when
            final User results = authService.register(registerRequest);
            //then
            assertNotNull(results);
            verify(userRepository, times(1)).save(any(User.class));
        }
    }
}