package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @EmbeddedId
    private CartId id;

    private Integer quantity;
    private Boolean isWishlistItem;

    @OneToOne
    @MapsId("customerId")
    private Customer customer;

    @OneToMany
    @MapsId("productVariationId")
    private List<ProductVariation> productVariation;

}
