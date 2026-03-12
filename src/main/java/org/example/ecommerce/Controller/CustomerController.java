package org.example.ecommerce.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.ResetPasswordDto;
import org.example.ecommerce.DTOS.Response.AddressResponse;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.CustomerProfileViewDto;
import org.example.ecommerce.Service.CustomerService;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
    @PutMapping("/update-password")
    public BasicResponse updateMyPassword(HttpServletRequest request, @Valid @RequestBody ResetPasswordDto passwordDTO,
                                     @RequestHeader(name = "Accept-Language", required = false) Locale locale){

        String token = request.getHeader("Authorization").substring(7);

        customerService.updateProfilePassword(token,passwordDTO.getPassword(), passwordDTO.getConfirmPassword());
        String response = messageSource.getMessage("message.updated.password",null,locale);
        return new BasicResponse(response,true);

    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/get-address")
    public List<AddressResponse> getAllAddressOfCustomer(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);

        return customerService.getAllCustomerAddress(token);
    }



    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/profile-details")
    public CustomerProfileViewDto viewProfile(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        return customerService.getMyProfile(token);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/update-profile")
    public BasicResponse updateCustomerProfile(
            @RequestParam(value = "firstName", required = false)
            @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "First name should only contain alphabets and spaces, no leading/trailing spaces.")
            String firstName,

            @RequestParam(value = "lastName", required = false)
            @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "Last name should only contain alphabets and spaces, no leading/trailing spaces.")
            String lastName,

            @RequestParam(value = "middleName", required = false)
            @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "Middle name should only contain alphabets and spaces, no leading/trailing spaces.")
            String middleName,

            @RequestParam(value = "contact", required = false)
            @Valid @Pattern(regexp = "^[0-9]{10}$", message = "Contact number should be exactly 10 digits.")
            String contact,

            @RequestParam(value = "image", required = false)
            MultipartFile image,
            HttpServletRequest request,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale
    ){

        String token = request.getHeader("Authorization").substring(7);

        customerService.updateCustomerProfileFields(token,firstName,lastName,middleName,contact,image);
        String response = messageSource.getMessage("message.updated.customerprofile",null,locale);
        return new BasicResponse(response,true);

    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/update-address/{id}")
    public BasicResponse updateMyAddress(
            @PathVariable(value = "id") Long id,
            @RequestParam(value = "city", required = false)
            @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "City must only contain letters and single spaces between words.")
            String city,

            @RequestParam(value = "state", required = false)
            @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "State must only contain letters and single spaces between words.")
            String state,

            @RequestParam(value = "addressLine", required = false)
            @Size(min = 5, max = 255, message = "Address line must be between 5 and 255 characters.")
            String addressLine,

            @RequestParam(value = "label", required = false)
            @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "AddressType must only contain letters and single spaces between words.")
            String label,

            @RequestParam(value = "country", required = false)
            @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "Country must only contain letters and single spaces between words.")
            String country,

            @RequestParam(value = "zipCode", required = false)
            @Digits(integer = 6, fraction = 0, message = "Zip code must be a numeric value with up to 6 digits.")
            Integer zipCode,
            HttpServletRequest request,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale
    ){

        String token = request.getHeader("Authorization").substring(7);

        customerService.updateAddress(id,city,state,addressLine,label,country,zipCode,token);
        String response = messageSource.getMessage("message.updated.customeraddress",null,locale);
        return new BasicResponse(response,true);
    }




}


