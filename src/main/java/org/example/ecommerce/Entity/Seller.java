package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_USER_SELLER"))
public class Seller extends User{

    @Column(unique = true, nullable = false)
    private String gst;
    @Column(unique = true, nullable = false)
    private String companyName;
    private String companyContact;

    @OneToMany(mappedBy = "seller")
    private List<Product> products;

}
