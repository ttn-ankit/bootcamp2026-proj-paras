package org.example.ecommerce.Controller;

import jakarta.validation.Valid;
import org.example.ecommerce.DTOS.Request.SellerDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.GlobalExceptions.PasswordDoesNotMatchException;
import org.example.ecommerce.Service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Validated
@RequestMapping("/api/user")
public class RegisterSeller {

    @Autowired
    private SellerService service;
    @Autowired
    private MessageSource messageSource;

    @PostMapping("/register-seller")
    public BasicResponse registerSeller(@Valid @RequestBody SellerDto sellerDto, @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        if(!sellerDto.getPassword().equals(sellerDto.getConfirmPassword())){
            throw new PasswordDoesNotMatchException("Password and Confirm Password Does not match");
        }
        service.registerSeller(sellerDto);
        String response = messageSource.getMessage("message.seller.register", null, locale);
        return new BasicResponse(response, true);
    }
}
