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
public class Cart {

    @EmbeddedId
     CartId id;

     Integer quantity;
     Boolean isWishlistItem;

    @OneToOne
    @MapsId("customerId")
     Customer customer;

    @OneToMany
    @MapsId("productVariationId")
     List<ProductVariation> productVariation;

}
