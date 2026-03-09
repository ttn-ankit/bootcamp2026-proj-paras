package org.example.ecommerce.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.ResetPasswordDto;
import org.example.ecommerce.DTOS.Response.AddressResponse;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.GlobalExceptions.PasswordDoesNotMatchException;
import org.example.ecommerce.Service.CustomerService;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Validated
@RequestMapping("/api/customer")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerController {

    CustomerService customerService;
    MessageSource messageSource;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add-address")
    public BasicResponse addNewAddressForCustomer(@Valid @RequestBody AddressResponse address,
                                                  HttpServletRequest request,
                                                  @RequestHeader(name = "Accept-Language", required = false) Locale locale){

        String token = request.getHeader("Authorization").substring(7);

        customerService.addNewCustomerAddress(token,address);

        String response = messageSource.getMessage("message.add.customeraddress",null,locale);
        return new BasicResponse(response,true);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/delete-address/{id}")
    public BasicResponse deleteExistingAddress(@PathVariable("id") Long id, HttpServletRequest request,
                                          @RequestHeader(name = "Accept-Language", required = false) Locale locale){

        String token = request.getHeader("Authorization").substring(7);

        customerService.deletedThisAddress(id,token);

        String response = messageSource.getMessage("message.delete.customeraddress",null,locale);
        return new BasicResponse(response,true);

    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/update-customer-password")
    public BasicResponse updateMyPassword(HttpServletRequest request, @Valid @RequestBody ResetPasswordDto passwordDTO,
                                     @RequestHeader(name = "Accept-Language", required = false) Locale locale){

        if(!passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())){
            throw new PasswordDoesNotMatchException("Passwords do not match.");
        }
        String token = request.getHeader("Authorization").substring(7);

        customerService.updateProfilePassword(token,passwordDTO.getPassword());
        String response = messageSource.getMessage("message.updated.password",null,locale);
        return new BasicResponse(response,true);

    }

}


