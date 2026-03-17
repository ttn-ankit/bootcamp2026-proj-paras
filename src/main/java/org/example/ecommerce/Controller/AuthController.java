package org.example.ecommerce.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.CustomerDto;
import org.example.ecommerce.DTOS.Request.LoginRequest;
import org.example.ecommerce.DTOS.Request.ResetPasswordDto;
import org.example.ecommerce.DTOS.Request.SellerDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.LoginResponse;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.example.ecommerce.Security.JWTService;
import org.example.ecommerce.Service.*;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    CustomerService customerService;
    MessageSource messageSource;
    SellerService service;

    AuthenticationManager authenticationManager;
    AccessTokenService tokenService;
    CustomUserDetailsService userDetailsService;
    LogoutService logoutService;
    ForgotPasswordService passwordService;
    JWTService jwtService;

    @PostMapping("/register-customer")
    public BasicResponse registerCustomer(@Valid @RequestBody CustomerDto customerDto){
        if(!customerDto.getPassword().equals(customerDto.getConfirmPassword())){
            throw new APIException("Password and Confirm Password Does not match", HttpStatus.BAD_REQUEST);
        }
        return customerService.registerCustomer(customerDto);
    }

    @PutMapping("/verify")
    public BasicResponse activateRegisteredUser(@PathParam("token") String token, @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        return customerService.activateCustomer(token,locale);
    }

    @PostMapping("/resend")
    public BasicResponse reSendActivationLink(@RequestBody Map<String, Object> requestBody, @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        String email = (String) requestBody.get("email");
        return customerService.reSendActivationLink(email, locale);
    }

    @PostMapping("/register-seller")
    public BasicResponse registerSeller(@Valid @RequestBody SellerDto sellerDto){
        return service.registerSeller(sellerDto);
    }

    @PostMapping("/login")
    public LoginResponse loginCustomer(@Valid @RequestBody LoginRequest loginRequest, @RequestHeader(name = "Accept-Language", required = false) Locale locale){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()
                )
        );
        String refreshToken = jwtService.generateRefreshToken(loginRequest.getEmail());
        String token = jwtService.generateAccessToken(loginRequest.getEmail());
        tokenService.saveToken(token,refreshToken);
        userDetailsService.setInvalidCountTo(0,loginRequest.getEmail());
        return new LoginResponse(messageSource.getMessage("message.welcome",null,locale),token,refreshToken);
    }

    @PostMapping("/logout")
    public BasicResponse logout(HttpServletRequest request, @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        String token = request.getHeader("Authorization").substring(7);
        logoutService.logoutUser(token);
        String response = messageSource.getMessage("message.logout", null, locale);
        return new BasicResponse(response, 200);
    }

    @PostMapping("/forgot-password")
    public BasicResponse forgetPassword(@RequestParam("email") String email){
        return passwordService.processForgotPassword(email);
    }

    @PutMapping("/resetPassword")
    public BasicResponse resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto,
                                       @PathParam("token") String token){
        return passwordService.setPassword(token, resetPasswordDto);
    }
}
