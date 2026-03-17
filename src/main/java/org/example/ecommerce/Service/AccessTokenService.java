package org.example.ecommerce.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.Entity.AccessToken;
import org.example.ecommerce.Repository.AccessTokenRepo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccessTokenService {
    AccessTokenRepo tokenRepo;

    public boolean findToken(String token){
        AccessToken databaseToken = tokenRepo.findByToken(token);
        return databaseToken != null;
    }
    public void saveToken(String token,String refreshToken,String email){
        tokenRepo.save(new AccessToken(token,refreshToken,email));
    }

}
