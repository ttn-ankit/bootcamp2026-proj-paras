package org.example.ecommerce.Controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.Service.ForgotPasswordService;
import org.springframework.context.MessageSource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Validated
@RestController
@RequestMapping("/api/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ForgotController {

    ForgotPasswordService passwordService;
    MessageSource messageSource;

    @PostMapping("/forgot-password")
    public BasicResponse forgetPassword(@RequestParam("email") String email, @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        passwordService.processForgotPassword(email);
        String response = messageSource.getMessage("message.forgetpassword",null,locale);
        return new BasicResponse(response,true);
    }


}
