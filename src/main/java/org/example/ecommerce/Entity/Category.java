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
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

     String name;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
     Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
     List<Category> subCategories;

    @OneToMany(mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
     List<CategoryMetadataFieldValue> metadataFieldValues;

    @OneToMany(mappedBy = "category")
     List<Product> products;
}
