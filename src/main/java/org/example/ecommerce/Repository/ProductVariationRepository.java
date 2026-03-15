package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.Product;
import org.example.ecommerce.Entity.ProductVariation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation,Long> {
    List<ProductVariation> findAllByProduct(Product product, Pageable pageable);


    List<ProductVariation> findAllByProduct(Product product);

    @Query("SELECT MAX(pv.price) FROM ProductVariation pv WHERE pv.product IN :products")
    Long findMaxPriceByProductIn(@Param("products") List<Product> products);
    @Query("SELECT MIN(pv.price) FROM ProductVariation pv WHERE pv.product IN :products")
    Long findMinPriceByProductIn(@Param("products") List<Product> products);

    Page<ProductVariation> findAll(Specification<ProductVariation> finalSpec, Pageable pageable);
}