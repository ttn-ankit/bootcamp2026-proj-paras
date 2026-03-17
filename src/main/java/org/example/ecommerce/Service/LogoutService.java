package org.example.ecommerce.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LogoutService {

     AccessTokenService tokenService;

    public BasicResponse logoutUser(String token) {
        tokenService.deleteToken(token);
        return new BasicResponse("Logout Success", 200);
    }
}
