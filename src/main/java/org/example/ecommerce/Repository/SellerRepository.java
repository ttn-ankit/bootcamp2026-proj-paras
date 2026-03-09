package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SellerRepository extends JpaRepository<Seller, Long> {
    Seller findByEmail(String email);
    @Query("SELECT s FROM Seller s WHERE s.email like %:email%")
    public Page<Seller> findAll(Pageable pageable, @Param("email") String email);
}
