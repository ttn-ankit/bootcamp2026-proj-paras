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
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.SellerProfileViewDto;
import org.example.ecommerce.GlobalExceptions.PasswordDoesNotMatchException;
import org.example.ecommerce.Service.SellerService;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Validated
@RestController
@RequestMapping("/api/seller")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SellerController {

    SellerService sellerService;
    MessageSource messageSource;

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("update-password")
    public BasicResponse updatePassword(@RequestBody @Valid ResetPasswordDto resetPasswordDTO, HttpServletRequest request,
                                        @RequestHeader(name = "Accept-Language", required = false) Locale locale){

        if(!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmPassword())){
            throw new PasswordDoesNotMatchException("password and confirm password should match");
        }
        String token = request.getHeader("Authorization").substring(7);
        sellerService.updateMyPassword(token  , resetPasswordDTO.getPassword());
        String response = messageSource.getMessage("message.seller.password.updated",null,locale);
        return new BasicResponse(response,true);

    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/profile-details")
    public SellerProfileViewDto viewMyProfileDetails(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        return sellerService.getMyProfile(token);

    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/update/address/{id}")
    public BasicResponse updateAddress(
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
        sellerService.updateSellerAddress(id,city,state,addressLine,label,country,zipCode,token);
        String response = messageSource.getMessage("message.seller.address.updated",null,locale);
        return new BasicResponse(response,true);

    }


    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/update-profile")
    public BasicResponse updateMyProfile(
            @RequestParam(value = "firstName", required = false)
            @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "First name should only contain alphabets and spaces, no leading/trailing spaces.")
            String firstName,

            @RequestParam(value = "lastName", required = false)
            @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "Last name should only contain alphabets and spaces, no leading/trailing spaces.")
            String lastName,

            @RequestParam(value = "middleName", required = false)
            @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "Middle name should only contain alphabets and spaces, no leading/trailing spaces.")
            String middleName,

            @RequestParam(value = "companyContact", required = false)
            @Pattern(regexp = "^[0-9]{10}$", message = "Contact number should be exactly 10 digits.")
            String contact,

            @RequestParam(value = "gst", required = false)
            @Pattern(regexp = "^[0-9A-Za-z]{15}$", message = "GST should be a valid 15-character alphanumeric code.")
            String gst,

            @RequestParam(value = "companyName", required = false)
            @Pattern(regexp = "^[A-Za-z ]{3,}$", message = "Company name should only contain alphabets and spaces, at least 3 characters long.")
            String companyName,

            @RequestParam(value = "image", required = false)
            MultipartFile image,

            HttpServletRequest request,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale
    ){
        String token = request.getHeader("Authorization").substring(7);
        sellerService.updateMyProfile(token,firstName,lastName,middleName,gst,companyName,contact,image);

        String response = messageSource.getMessage("message.seller.profile.updated",null,locale);
        return new BasicResponse(response,true);
    }


}
