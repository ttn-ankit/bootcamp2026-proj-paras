package org.example.ecommerce.DTOS.Request;


import jakarta.validation.Valid;
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
public class AddressDto {

     Long id;

    @NotBlank(message = "city can not be blank")
    @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",message = "give valid city name")
     String city;
    @NotBlank(message = "state can not be blank")
    @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",message = "give valid state name")
     String state;
    @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",message = "give valid country name")
    @NotBlank(message = "country can not be blank")
     String country;
     String addressLine;
    @NotNull(message = "zip code can no be blank")
     Integer zipCode;
     String label;
    @Valid
     UserDto userDTO;
}
