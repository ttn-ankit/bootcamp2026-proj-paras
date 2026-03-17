package org.example.ecommerce.DTOS.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAddressDto {

     Long id;

     String city;
     String state;
     String country;
     String addressLine;
     String zipCode;
     String label;

}
