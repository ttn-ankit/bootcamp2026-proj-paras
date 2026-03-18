package org.example.ecommerce.Entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActivationToken {

    @Id
     String email;

    @Column(unique = true, nullable = false)
     String token;


}
