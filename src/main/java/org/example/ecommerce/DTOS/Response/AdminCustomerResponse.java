package org.example.ecommerce.DTOS.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCustomerResponse {
         Long id;
         String fullName;
         String email;
         Boolean isActive;
    }


