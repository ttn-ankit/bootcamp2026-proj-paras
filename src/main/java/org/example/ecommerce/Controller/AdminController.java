package org.example.ecommerce.Controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.AdminCustomerResponse;
import org.example.ecommerce.DTOS.Response.AdminSellerResponse;
import org.example.ecommerce.Service.CustomerService;
import org.example.ecommerce.Service.SellerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/admin")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminController {

     CustomerService customerService;
     SellerService sellerService;

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate-customer/{id}")
    public BasicResponse activateCustomer(@PathVariable("id") Long id, @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        return customerService.activateCustomerById(id,locale);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate-seller/{id}")
    public BasicResponse activateSeller(@PathVariable("id") Long id,@RequestHeader(name = "Accept-Language", required = false) Locale locale){
        return sellerService.activateSellerById(id,locale);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deactivate-customer/{id}")
    public BasicResponse deActivateCustomer(@PathVariable("id") Long id,@RequestHeader(name = "Accept-Language", required = false) Locale locale){
        return customerService.deActivateCustomerById(id,locale);

    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deactivate-seller/{id}")
    public BasicResponse deActivateSeller(@PathVariable("id") Long id,@RequestHeader(name = "Accept-Language", required = false) Locale locale){
        return sellerService.DeActivateSellerById(id,locale);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/customers")
    public List<AdminCustomerResponse> getAllCustomers(
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageOffset",defaultValue = "0") Integer pageOffset,
            @RequestParam(value = "sort",defaultValue = "id") String sort,
            @RequestParam(value = "email",defaultValue = "%") String email
    ){
        return customerService.getAllCustomer(pageSize,pageOffset,sort,email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/sellers")
    public List<AdminSellerResponse> getAllSellers(
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageOffset",defaultValue = "0") Integer pageOffset,
            @RequestParam(value = "sort",defaultValue = "id") String sort,
            @RequestParam(value = "email",defaultValue = "%") String email
    ){
        return sellerService.getAllSeller(pageSize,pageOffset,sort,email);
    }
}
