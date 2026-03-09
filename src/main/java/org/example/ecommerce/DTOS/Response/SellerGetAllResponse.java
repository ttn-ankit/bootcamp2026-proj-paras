package org.example.ecommerce.DTOS.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellerGetAllResponse {

     String fullName;
     Long id;
     String email;
     String companyName;
     Boolean isActive;
     AddressResponse companyAddress;
     String contact;
}
