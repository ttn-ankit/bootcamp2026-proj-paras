package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.Category;
import org.example.ecommerce.Entity.Product;
import org.example.ecommerce.Entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Product findByBrandAndCategoryAndSeller(String brand, Category category, Seller seller);

    Page<Product> findAllBySellerId(Long id, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Product findProductById(@Param("id") Long id);


    List<Product> findAllByCategoryIdAndIsActiveTrue(Long id, Pageable pageable);

    Page<Product> findByCategoryOrBrandAndIdNot(Category category, String brand, Long id, Pageable pageable);

    List<Product> findByCategoryId(Long aLong);

    boolean existsByCategoryId(Long parentId);

    Page<Product> findAllBySellerId(Specification<Product> spec, Long id, Pageable pageable);

    Page<Product> findAll(Specification<Product> finalSpec, Pageable pageable);
}

