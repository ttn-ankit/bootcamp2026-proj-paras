package org.example.ecommerce.Entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "category_metadata_field_value",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"category_id", "metadata_field_id", "value"}
                )
        }
)
public class CategoryMetadataFieldValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metadata_field_id", nullable = false)
    private CategoryMetadataField metadataField;
}
