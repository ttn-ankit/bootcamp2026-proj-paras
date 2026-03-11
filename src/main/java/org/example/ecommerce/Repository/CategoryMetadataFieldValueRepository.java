package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.Category;
import org.example.ecommerce.Entity.CategoryMetadataFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMetadataFieldValueRepository extends JpaRepository<CategoryMetadataFieldValue, Long> {

    List<CategoryMetadataFieldValue> findByCategoryId(Long id);

    List<CategoryMetadataFieldValue> findByCategory(Category category);
}
