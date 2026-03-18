package org.example.ecommerce.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.example.ecommerce.Repository.AccessTokenRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LogoutService {

     AccessTokenRepo tokenRepo;

    public BasicResponse logoutUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new APIException("Missing or invalid Authorization token", HttpStatus.UNAUTHORIZED);
        }
        String email = auth.getName();
        tokenRepo.deleteByEmail(email);
        return new BasicResponse("Logout Success", 200);
    }
}
