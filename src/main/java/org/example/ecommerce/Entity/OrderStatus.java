package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.Entity.Enum.OrderStatusEnum;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
     OrderStatusEnum fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
     OrderStatusEnum toStatus;

     String transitionNotes;
     LocalDateTime transitionDate;

    @ManyToOne
    @JoinColumn(name = "order_product_id")
     OrderProduct orderProduct;
}
