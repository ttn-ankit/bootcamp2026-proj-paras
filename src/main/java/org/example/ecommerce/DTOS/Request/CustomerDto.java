package org.example.ecommerce.DTOS.Request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDto extends UserDto {
    @Pattern(regexp =  "^[6-9][0-9]{9}$" ,message = "Give Valid Contact number")
     String contact;

    @Valid
     List<AddressDto> addressesDTO;
}
