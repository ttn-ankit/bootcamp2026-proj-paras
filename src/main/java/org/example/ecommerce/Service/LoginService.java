package org.example.ecommerce.Service;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.DTOS.Request.LoginRequest;
import org.example.ecommerce.DTOS.Response.LoginResponse;
import org.example.ecommerce.Security.JWTService;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final AccessTokenService accessTokenService;
    private final CustomUserDetailsService userDetailsService;
    private final MessageSource messageSource;

    public LoginResponse login(LoginRequest loginRequest, Locale locale) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String refreshToken = jwtService.generateRefreshToken(loginRequest.getEmail());
        String accessToken = jwtService.generateAccessToken(loginRequest.getEmail());

        accessTokenService.saveToken(accessToken, refreshToken, loginRequest.getEmail());
        userDetailsService.setInvalidCountTo(0, loginRequest.getEmail());

        String msg = messageSource.getMessage("message.welcome", null, locale);
        return new LoginResponse(msg, accessToken, refreshToken);
    }
}