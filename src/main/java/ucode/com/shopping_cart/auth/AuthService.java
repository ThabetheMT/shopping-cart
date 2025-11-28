package ucode.com.shopping_cart.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucode.com.shopping_cart.auth.dto.AuthResponse;
import ucode.com.shopping_cart.auth.dto.LoginRequest;
import ucode.com.shopping_cart.auth.dto.RegisterRequest;
import ucode.com.shopping_cart.common.custom.CustomUserDetails;
import ucode.com.shopping_cart.common.custom.CustomUserDetailsService;
import ucode.com.shopping_cart.common.jwt.JwtUtil;
import ucode.com.shopping_cart.user.Role;
import ucode.com.shopping_cart.user.User;
import ucode.com.shopping_cart.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.CUSTOMER)
                .build();

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String jwt = jwtUtil.generateToken(userDetails);

        CustomUserDetails customDetails = (CustomUserDetails) userDetails;
        User user = customDetails.getUser();

        return new AuthResponse(
                jwt,
                "Login successful",
                user.getCustomerId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}