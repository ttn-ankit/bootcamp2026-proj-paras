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
    @Pattern(regexp = "^[A-Za-z]{2,}(?: [A-Za-z]{2,})*$",message = "give valid name")
     String name;
     Boolean isCancellable;
     Boolean isRefundable;
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$",message = "give valid name")
     String description;

}
