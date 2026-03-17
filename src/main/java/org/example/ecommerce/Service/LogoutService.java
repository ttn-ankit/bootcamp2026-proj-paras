package org.example.ecommerce.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.Repository.AccessTokenRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LogoutService {

     AccessTokenRepo tokenRepo;

    public BasicResponse logoutUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        tokenRepo.deleteByEmail(email);
        return new BasicResponse("Logout Success", 200);
    }
}
