package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;


     String city;
     String state;
     String country;
     String addressLine;
     String zipCode;
     Boolean isDeleted = false;
     String label;

    @ManyToOne
    @JoinColumn(name = "user_id")
     User user;

}
