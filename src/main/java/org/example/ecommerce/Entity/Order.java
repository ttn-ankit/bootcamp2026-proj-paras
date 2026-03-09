package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.Entity.Enum.PaymentMethodEnum;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

     Double amountPaid;
     LocalDateTime dateCreated;

    @Enumerated(EnumType.STRING)
     PaymentMethodEnum paymentMethod;

    @ManyToOne
    @JoinColumn(name = "customer_id")
     Customer customer;

    @OneToMany(mappedBy = "order")
     List<OrderProduct> orderProducts;
}
