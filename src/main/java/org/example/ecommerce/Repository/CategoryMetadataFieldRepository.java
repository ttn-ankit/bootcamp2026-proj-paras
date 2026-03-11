package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.CategoryMetadataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField, Long>, JpaSpecificationExecutor<CategoryMetadataField> {

    boolean existsByName(String name);

    Page<CategoryMetadataField> findAll(Specification<CategoryMetadataField> spec, Pageable pageable);

}
