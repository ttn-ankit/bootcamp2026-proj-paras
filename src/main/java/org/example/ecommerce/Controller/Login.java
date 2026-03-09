package org.example.ecommerce.Controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.LoginRequest;
import org.example.ecommerce.DTOS.Response.LoginResponse;
import org.example.ecommerce.Service.AccessTokenService;
import org.example.ecommerce.Service.CustomUserDetailsService;
import org.example.ecommerce.Tokens.JwtLogin;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class Login {


    AuthenticationManager authenticationManager;
    AccessTokenService tokenService;
    CustomUserDetailsService userDetailsService;
    MessageSource messageSource;

    @PostMapping("/login")
    public LoginResponse loginCustomer(@Valid @RequestBody LoginRequest loginRequest, @RequestHeader(name = "Accept-Language", required = false) Locale locale){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),loginRequest.getPassword()
                )
        );
        String token = JwtLogin.generateLoginAccessToken(loginRequest.getEmail());
        String refreshToken = JwtLogin.generateLoginRefreshToken(loginRequest.getEmail());
        tokenService.saveToken(token,refreshToken);
        userDetailsService.setInvalidCountTo(0,loginRequest.getEmail());
        return new LoginResponse(""+messageSource.getMessage("message.welcome",null,locale),token,refreshToken);
    }

}
