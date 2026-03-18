package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.ForgetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ForgetPasswordRepository extends JpaRepository<ForgetPasswordToken, String> {
    ForgetPasswordToken findByEmail(String email);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("DELETE FROM ForgetPasswordToken f WHERE f.email = :email")
    int deleteByEmail(@Param("email") String email);
}
