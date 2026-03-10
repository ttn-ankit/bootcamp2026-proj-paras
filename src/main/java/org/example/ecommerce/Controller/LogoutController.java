package org.example.ecommerce.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.Service.LogoutService;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LogoutController {
     LogoutService logoutService;
     MessageSource messageSource;

    @PostMapping("/logout")
    public BasicResponse logout(HttpServletRequest request,@RequestHeader(name = "Accept-Language", required = false) Locale locale){
        String token = request.getHeader("Authorization").substring(7);
        logoutService.logoutUser(token);
        String response = messageSource.getMessage("message.logout", null, locale);
        return new BasicResponse(response, true);
    }
}
