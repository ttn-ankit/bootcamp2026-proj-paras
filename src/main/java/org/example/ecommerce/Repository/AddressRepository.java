package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
