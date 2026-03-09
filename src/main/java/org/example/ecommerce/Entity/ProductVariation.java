package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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


}
