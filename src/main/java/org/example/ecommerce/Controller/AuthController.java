package org.example.ecommerce.Controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.CustomerDto;
import org.example.ecommerce.DTOS.Request.LoginRequest;
import org.example.ecommerce.DTOS.Request.ResetPasswordDto;
import org.example.ecommerce.DTOS.Request.SellerDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.LoginResponse;
import org.example.ecommerce.Service.*;
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
    SellerService sellerService;
    LoginService loginService;
    LogoutService logoutService;
    ForgotPasswordService passwordService;

    @PostMapping("/register-customer")
    public BasicResponse registerCustomer(@Valid @RequestBody CustomerDto customerDto){
        return customerService.registerCustomer(customerDto);
    }

    @PutMapping("/verify")
    public BasicResponse activateRegisteredUser(@RequestParam("token") String token, @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        return customerService.activateCustomer(token,locale);
    }

    @PostMapping("/resend")
    public BasicResponse reSendActivationLink(@RequestBody Map<String, Object> requestBody, @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        String email = (String) requestBody.get("email");
        return customerService.reSendActivationLink(email, locale);
    }

    @PostMapping("/register-seller")
    public BasicResponse registerSeller(@Valid @RequestBody SellerDto sellerDto){
        return sellerService.registerSeller(sellerDto);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest,
                               @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        return loginService.login(loginRequest, locale);
    }

    @PostMapping("/logout")
    public BasicResponse logout(){
        return logoutService.logoutUser();
    }

    @PostMapping("/forgot-password")
    public BasicResponse forgetPassword(@RequestParam("email") String email){
        return passwordService.processForgotPassword(email);
    }

    @PutMapping("/resetPassword")
    public BasicResponse resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto,
                                       @RequestParam("token") String token){
        return passwordService.setPassword(token, resetPasswordDto);
    }
}
