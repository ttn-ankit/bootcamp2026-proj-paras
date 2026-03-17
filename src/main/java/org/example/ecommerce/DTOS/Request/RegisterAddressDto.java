package org.example.ecommerce.DTOS.Request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterAddressDto {

     Long id;

    @NotBlank(message = "city can not be blank")
     String city;
    @NotBlank(message = "state can not be blank")
     String state;
    @NotBlank(message = "country can not be blank")
     String country;
     String addressLine;
    @NotNull(message = "zip code can no be blank")
    @Pattern(regexp = "^[0-9]{6}$", message = "Invalid Zipcode")
     String zipCode;
     String label; //enum
}
