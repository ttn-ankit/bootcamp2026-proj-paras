package org.example.ecommerce.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Response.LoginResponse;
import org.example.ecommerce.Entity.AccessToken;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.example.ecommerce.Repository.AccessTokenRepo;
import org.example.ecommerce.Security.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccessTokenService {
    AccessTokenRepo tokenRepo;
    JWTService jwtService;

    public boolean findToken(String token){
        AccessToken databaseToken = tokenRepo.findByToken(token);
        return databaseToken != null;
    }
    public void saveToken(String token,String refreshToken,String email){
        tokenRepo.save(new AccessToken(token,refreshToken,email));
    }

    public LoginResponse regenerateLoginAccess(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        if (!jwtService.validateToken(refreshToken, email)) {
            throw new APIException("Invalid Token", HttpStatus.BAD_REQUEST);
        }
        AccessToken existing = tokenRepo.findByRefreshToken(refreshToken);
        if (existing == null) {
            throw new APIException("Invalid Token", HttpStatus.BAD_REQUEST);
        }
        if (!existing.getEmail().equals(email)) {
            throw new APIException("Invalid Token", HttpStatus.BAD_REQUEST);
        }
        String newAccessToken = jwtService.generateAccessToken(email);
        tokenRepo.deleteByToken(existing.getToken());
        tokenRepo.save(new AccessToken(newAccessToken, existing.getRefreshToken(), existing.getEmail()));
        return new LoginResponse("New Access Token", newAccessToken, refreshToken);
    }

}
