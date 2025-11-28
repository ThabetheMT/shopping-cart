package ucode.com.shopping_cart.order;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ucode.com.shopping_cart.product.Product;
import ucode.com.shopping_cart.user.User;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Order {
    @Id
    private Long id;
    private Double totalPrice;
    private Date OrderDate;
    private Date EstimatedDeliveryDate;
    private Date DeliveryDate;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;
}
