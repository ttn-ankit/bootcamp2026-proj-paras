package org.example.ecommerce.DTOS.Response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {

    private Long id;

    private String city;
    private String state;
    private String country;
    private String addressLine;
    private Integer zipCode;
    private String label;

}
