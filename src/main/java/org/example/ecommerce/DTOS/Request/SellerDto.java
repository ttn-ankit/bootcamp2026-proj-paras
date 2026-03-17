package org.example.ecommerce.DTOS.Request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellerDto extends UserDto{
    @NotBlank(message = "GST should not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9]{15}$",message = "gst should be valid")
     String gst;

    @Pattern(regexp =  "^[6-9][0-9]{9}$" ,message = "Give Valid Contact number")
     String companyContact;

    @NotBlank(message = "Company Name can not be blank")
     String companyName;

    @NotNull(message = "address for the seller is required")
    @Valid
    RegisterAddressDto addressesDTO;
}
