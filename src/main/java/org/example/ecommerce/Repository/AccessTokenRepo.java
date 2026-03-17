package org.example.ecommerce.Repository;

import jakarta.transaction.Transactional;
import org.example.ecommerce.Entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenRepo extends JpaRepository<AccessToken,String> {

     AccessToken findByToken(String token);
     @Transactional
     @Modifying
     void deleteByEmail(String email);
}