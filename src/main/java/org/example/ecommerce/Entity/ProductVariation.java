package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String,String> metadata;

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
