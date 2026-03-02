package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ecommerce.Entity.Enum.OrderStatusEnum;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusEnum fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusEnum toStatus;

    private String transitionNotes;
    private LocalDateTime transitionDate;

    @ManyToOne
    @JoinColumn(name = "order_product_id")
    private OrderProduct orderProduct;
}
