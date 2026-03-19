package org.example.ecommerce.DTOS.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProduct {

    @NotNull(message = "id must not be null")
     Long id;
    @Pattern(regexp = "^[A-Za-z0-9]+(?:[ -][A-Za-z0-9()]+)*$",message = "give valid name")
     String name;
     Boolean isCancellable;
     Boolean isRefundable;
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$",message = "give valid name")
     String description;

}
