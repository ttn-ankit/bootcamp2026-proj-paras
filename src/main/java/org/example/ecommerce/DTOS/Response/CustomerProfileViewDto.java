package org.example.ecommerce.DTOS.Response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerProfileViewDto {

     Long id;
     String firstName;
     String lastName;
     Boolean isActive;
     String contact;
     String image;
}
