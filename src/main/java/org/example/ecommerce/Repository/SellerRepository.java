package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SellerRepository extends JpaRepository<Seller, Long> {
    Seller findByEmail(String email);
}
