package org.example.ecommerce.DTOS.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.AddressDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellerProfileViewDto {

     Long id;
     String firstName;
     String lastName;
     Boolean isActive;
     String companyContact;
     String companyName;
     String gst;
     AddressDto address;
     String image;
}
