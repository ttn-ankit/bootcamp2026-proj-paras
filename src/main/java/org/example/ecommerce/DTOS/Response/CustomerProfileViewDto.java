package org.example.ecommerce.DTOS.Response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfileViewDto {

    private Long id;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private String contact;
    private String image;
}
