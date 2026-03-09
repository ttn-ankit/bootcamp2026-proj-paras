package org.example.ecommerce.Controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.Emails.ForgotPassword;
import org.example.ecommerce.Entity.User;
import org.example.ecommerce.GlobalExceptions.AccountNotActiveException;
import org.example.ecommerce.GlobalExceptions.InvalidEmail;
import org.example.ecommerce.Service.ForgotPasswordService;
import org.example.ecommerce.Tokens.JwtForgot;
import org.springframework.context.MessageSource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ForgotController {

    ForgotPasswordService passwordService;
    ForgotPassword forgetPasswordEmail;
    MessageSource messageSource;

    @PostMapping("/forgot-password")
    public BasicResponse forgetPassword(@RequestBody Map<String, String> request, @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        String email = request.get("email");
        if(email == null || email.trim().isEmpty()){
            throw new InvalidEmail("email is not valid");
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            throw new InvalidEmail("email is not valid");
        }
        User user = passwordService.getForgetPasswordTokenUser(email);
        if(user!=null){
            if(user.getIsLocked()){
                throw new AccountNotActiveException("Account is locked. You can not reset password. contact Admin");
            }
            if(!user.getIsActive()){
                throw new AccountNotActiveException("Account is not active please contact admin" +
                        " or activate it by activation link");
            }
            String token = JwtForgot.generateForgetPasswordToken(email);
            passwordService.setForgetTokenInDataBase(email,token);
            forgetPasswordEmail.sendForgetPasswordEmail(email,token);
        }

        String response = messageSource.getMessage("message.forgetpassword",null,locale);
        return new BasicResponse(response,true);
    }


}
