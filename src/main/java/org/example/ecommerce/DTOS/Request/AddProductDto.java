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
public class AddProductDto {

     Long id;

    @NotBlank(message = "name can not be blank")
    @Pattern(regexp = "^[A-Za-z0-9]+(?:[ -][A-Za-z0-9()]+)*$",message = "give valid product name")
     String name;
    @NotBlank(message = "brand can not be blank")
    @Pattern(regexp = "^[A-Za-z0-9]+(?:[ -][A-Za-z0-9]+)*$",message = "give valid brand name")
     String brand;
    @NotNull(message = "category id should not be blank")
     Long categoryId;

     String description;
     Boolean isCancellable;
     Boolean isRefundable;
}
