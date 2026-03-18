package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "category_metadata_field")
public class CategoryMetadataField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

    @Column(nullable = false)
     String name;

    @OneToMany(mappedBy = "metadataField",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
     List<CategoryMetadataFieldValue> fieldValues;

}
