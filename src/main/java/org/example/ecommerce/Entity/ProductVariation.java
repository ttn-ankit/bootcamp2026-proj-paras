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
public class ProductVariation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

     Integer quantityAvailable;
     Double price;

    @Column(columnDefinition = "TEXT")
     String metadata;

     String primaryImageName;
     Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "product_id")
     Product product;

    @OneToMany(mappedBy = "productVariation")
    private List<Cart> cart;

    @OneToMany(mappedBy = "productVariation",cascade = CascadeType.ALL)
    private List<OrderProduct> orderProduct;

}
