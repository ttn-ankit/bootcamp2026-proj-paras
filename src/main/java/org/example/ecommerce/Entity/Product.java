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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

     String name;
     String description;
     String brand;
     Boolean isCancellable;
     Boolean isReturnable;
     Boolean isActive;
     Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "seller_id")
     Seller seller;

    @ManyToOne
    @JoinColumn(name = "category_id")
     Category category;

    @OneToMany(mappedBy = "product")
     List<ProductVariation> variations;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
     List<ProductReview> reviews;
}
