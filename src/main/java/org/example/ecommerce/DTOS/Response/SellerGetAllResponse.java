package org.example.ecommerce.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SellerGetAllResponse {

    private String fullName;
    private Long id;
    private String email;
    private String companyName;
    private Boolean isActive;
    private AddressResponse companyAddress;
    private String contact;
}
