package org.example.ecommerce.DTOS.Request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {

     Long id;

    @NotBlank(message = "city can not be blank")
     String city;
    @NotBlank(message = "state can not be blank")
     String state;
    @NotBlank(message = "country can not be blank")
     String country;
     String addressLine;
    @NotNull(message = "zip code can no be blank")
     Integer zipCode;
     String label;
}
