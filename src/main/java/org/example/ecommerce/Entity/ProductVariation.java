package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantityAvailable;
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private String primaryImageName;
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


}
