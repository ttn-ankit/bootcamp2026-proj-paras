package org.example.ecommerce.DTOS.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddCategoryDto {

    @NotBlank(message = "category name should not be blank")
    @Pattern(regexp = "^[a-zA-Z'&.,-]+(?: [a-zA-Z'&.,-]+)*$" , message = "field name must be valid and at least of 3 size")
     String name;

     Long parent_id;
}
