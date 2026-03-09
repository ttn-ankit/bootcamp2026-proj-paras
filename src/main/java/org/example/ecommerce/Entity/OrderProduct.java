package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

     Integer quantity;
     Double price;

    @ManyToOne
    @JoinColumn(name = "order_id")
     Order order;

    @ManyToOne
    @JoinColumn(name = "product_variation_id")
     ProductVariation productVariation;

    @OneToMany(mappedBy = "orderProduct")
     List<OrderStatus> statuses;
}
