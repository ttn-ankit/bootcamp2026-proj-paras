package org.example.ecommerce.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    @Autowired
    private AccessTokenService tokenService;

    public void logoutUser(String token) {
        tokenService.deleteToken(token);
    }
}
