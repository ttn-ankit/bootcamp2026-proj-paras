package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.Auditing.DataAudit;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Users")
public class User  extends DataAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

    @Column(nullable = false, unique = true)
     String email;

    @Column(nullable = false )
     String firstName;

     String middleName;

     String lastName;

     String password;

     Boolean isDeleted;
     Boolean isActive;
     Boolean isExpired;
     Boolean isLocked;

     Integer invalidAttemptCount;

     LocalDateTime passwordUpdateDate;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLE" ,
            joinColumns = @JoinColumn(name = "USER_ID"),foreignKey = @ForeignKey(name = "FK_USER_ROLE_USER"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID") , inverseForeignKey = @ForeignKey(name = "FK_USER_ROLE_ROLE"))
     List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
     List<Address> addresses;

}
