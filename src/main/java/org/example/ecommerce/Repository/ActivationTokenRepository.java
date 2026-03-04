package org.example.ecommerce.Repository;

import org.example.ecommerce.Entity.UserActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivationTokenRepository
        extends JpaRepository<UserActivationToken, String> {

    UserActivationToken findByEmail(String email);
}