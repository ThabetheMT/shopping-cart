package ucode.com.shopping_cart.common.custom;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ucode.com.shopping_cart.user.User;

import java.util.Collection;
import java.util.List;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    // Bonus: helpful method to get the full User entity if needed
    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert the Role enum to a GrantedAuthority
        // Spring Security expects roles to be prefixed with "ROLE_"
        String roleName = "ROLE_" + user.getRole().name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // You're using email as login identifier
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // You can add an "enabled" field later if needed
    }

    // Optional: get full name
    public String getFullName() {
        return user.getFirstName() + " " + user.getLastName();
    }
}
