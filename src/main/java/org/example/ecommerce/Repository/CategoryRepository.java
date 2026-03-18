package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    boolean existsByNameIgnoreCaseAndParentCategoryIsNull(String name);

    boolean existsByNameIgnoreCaseAndParentCategory(String name , Category parent);

    List<Category> findAllByParentCategoryId(Long id);

    List<Category> findAllByParentCategoryIdIsNull();

    boolean existsByParentCategoryId(Long id);

    Page<Category> findAll(Specification<Category> spec, Pageable pageable);
}
