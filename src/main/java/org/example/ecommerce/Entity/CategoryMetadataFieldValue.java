package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category_metadata_field_value")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryMetadataFieldValue {

    @EmbeddedId
     CategoryMetadataFieldValuesKey id;

    @Column(nullable = false)
     String value;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
     Category category;

    @ManyToOne
    @MapsId("categoryMetaDataFieldId")
    @JoinColumn(name = "metadata_field_id")
     CategoryMetadataField metadataField;
}
