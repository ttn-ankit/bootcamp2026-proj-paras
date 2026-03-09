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
@PrimaryKeyJoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_USER_SELLER"))
public class Seller extends User{

    @Column(unique = true, nullable = false)
     String gst;
    @Column(unique = true, nullable = false)
     String companyName;
     String companyContact;

    @OneToMany(mappedBy = "seller")
     List<Product> products;

}
