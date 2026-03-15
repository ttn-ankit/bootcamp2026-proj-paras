package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "product_review",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"customer_id", "product_id"}
                )
        }
)
public class ProductReview {

    @EmbeddedId
    private ProductReviewKey id;


    @ManyToOne
    @MapsId("customerId")
    @JoinColumn(name = "customer_id")
     Customer customer;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
     Product product;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String review;

    private LocalDateTime createdAt;
}
