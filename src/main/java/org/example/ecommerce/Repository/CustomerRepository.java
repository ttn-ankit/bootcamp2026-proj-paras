package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.email like %:email%")
    Page<Customer> findAll(Pageable pageable, @Param("email") String email);
}
