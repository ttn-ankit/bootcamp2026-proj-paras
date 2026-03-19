package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.ForgetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgetPasswordRepository extends JpaRepository<ForgetPasswordToken, String> {
    ForgetPasswordToken findByEmail(String email);
}
