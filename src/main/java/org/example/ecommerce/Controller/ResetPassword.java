package org.example.ecommerce.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.example.ecommerce.DTOS.Request.ResetPasswordDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.GlobalExceptions.PasswordDoesNotMatchException;
import org.example.ecommerce.Service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Validated
@RestController
@RequestMapping("/api/user")
public class ResetPassword {
    @Autowired
    private ForgotPasswordService passwordService;
    @Autowired
    private MessageSource messageSource;

    @PutMapping("/reset")
    public BasicResponse resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto,
                                       HttpServletRequest request, @PathParam("token") String token,
                                       @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        if(!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirmPassword())){
            throw new PasswordDoesNotMatchException("Password does not match");
        }
        passwordService.setPassword(token, resetPasswordDto.getPassword());
        String response = messageSource.getMessage("message.reset.password", null, locale);
        return new BasicResponse(response, true);
    }
}
