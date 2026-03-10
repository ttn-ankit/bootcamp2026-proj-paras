package org.example.ecommerce.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ecommerce.DTOS.Request.AddressDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SellerProfileViewDto {

    private Long id;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private String companyContact;
    private String companyName;
    private String gst;
    private AddressDto address;
    private String image;
}
