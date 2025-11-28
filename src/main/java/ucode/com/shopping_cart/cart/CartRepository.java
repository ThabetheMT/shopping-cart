// CartRepository.java
package ucode.com.shopping_cart.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserCustomerId(Long userId);
}
