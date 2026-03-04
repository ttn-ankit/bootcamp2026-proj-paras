package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@PrimaryKeyJoinColumn (name = "USER_ID", foreignKey = @ForeignKey( name = "FK_USER_CUSTOMER"))
public class Customer extends User{

     String contact;

    @OneToOne(mappedBy = "customer")
     Cart carts;

    @OneToMany(mappedBy = "customer")
     List<Order> orders;

    @OneToMany(mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
     List<ProductReview> reviews;
}
