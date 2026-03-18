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
public class CategoryMetadataFieldDto {

    @NotBlank(message = "field name should not be blank")
    @Pattern(regexp = "^(?!\\s)([a-zA-Z]+(?: [a-zA-Z]+)*)(?<=\\S)$", message = "Only alphabets allowed, one space in between max, min length 4")
     String name;
     Long id;

}
