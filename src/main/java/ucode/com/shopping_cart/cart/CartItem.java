// src/main/java/ucode/com/shopping_cart/cart/CartItem.java
package ucode.com.shopping_cart.cart;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    private Long productId;           // We'll link to Product later
    private String productName;
    private BigDecimal price;         // Price at time of adding
    private String imageUrl;

    private int quantity;

    @Transient
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
