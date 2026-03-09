package org.example.ecommerce.Repository;

import jakarta.transaction.Transactional;
import org.example.ecommerce.Entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AccessTokenRepo extends JpaRepository<AccessToken,String> {

     AccessToken findByToken(String token);

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM AccessToken t WHERE t.createdAt < :cutoffTime")
//    void deleteTokensOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
}