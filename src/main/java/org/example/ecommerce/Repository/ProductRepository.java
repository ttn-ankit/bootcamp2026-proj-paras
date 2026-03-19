package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.Category;
import org.example.ecommerce.Entity.Product;
import org.example.ecommerce.Entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Product findByBrandAndCategoryAndSeller(String brand, Category category, Seller seller);


    List<Product> findByCategoryId(Long aLong);

    boolean existsByCategoryId(Long parentId);

    Page<Product> findAll(Specification<Product> finalSpec, Pageable pageable);

    boolean existsByNameIgnoreCaseAndBrandIgnoreCaseAndCategoryAndSeller(
            String name, String brand, Category category, Seller seller
    );
    boolean existsByNameIgnoreCaseAndBrandIgnoreCaseAndCategoryAndSellerAndIdNot(
            String name, String brand, Category category, Seller seller, Long id
    );
}

