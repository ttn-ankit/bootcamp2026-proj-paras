package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.processing.Pattern;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Invalid email format", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    @NotBlank(message = "Email cannot be blank")
    @Column(nullable = false, unique = true)
    private String email;
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    private String middleName;

    private String lastName;
    @NotBlank
    @Size(min = 2, max = 50)
    private String password;

    private Boolean isDeleted;
    private Boolean isActive;
    private Boolean isExpired;
    private Boolean isLocked;

    private Integer invalidAttemptCount;

    private LocalDateTime passwordUpdateDate;

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles;

    @OneToOne(mappedBy = "user")
    private Customer customer;

    @OneToOne(mappedBy = "user")
    private Seller seller;

    @OneToMany(mappedBy = "user")
    private List<Address> addresses;

}
