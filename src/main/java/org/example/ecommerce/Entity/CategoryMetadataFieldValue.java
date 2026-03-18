package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "category_metadata_field_value",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"category_id", "metadata_field_id", "value"}
                )
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryMetadataFieldValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

    @Column(nullable = false)
     String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
     Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metadata_field_id", nullable = false)
     CategoryMetadataField metadataField;
}
