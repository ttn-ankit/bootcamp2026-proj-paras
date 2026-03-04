package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
     Role findByAuthority(String authority);
}
