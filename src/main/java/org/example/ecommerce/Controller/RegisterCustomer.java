package org.example.ecommerce.Controller;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.example.ecommerce.DTOS.Request.CustomerDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.GlobalExceptions.PasswordDoesNotMatchException;
import org.example.ecommerce.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Validated
public class RegisterCustomer {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private MessageSource messageSource;

    @PostMapping("/register-customer")
    public BasicResponse registerCustomer(@Valid @RequestBody CustomerDto customerDto, @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        if(!customerDto.getPassword().equals(customerDto.getConfirmPassword())){
            throw new PasswordDoesNotMatchException("Password and Confirm Password Does not match");
        }
        customerService.registerCustomer(customerDto);
        String response = messageSource.getMessage("message.customer.registered", null, locale);
        return new BasicResponse(response, true);
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

}
