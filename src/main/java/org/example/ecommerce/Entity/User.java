package org.example.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ecommerce.Auditing.DataAudit;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Users")
public class User  extends DataAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false )
    private String firstName;

    private String middleName;

    private String lastName;

    private String password;

    private Boolean isDeleted;
    private Boolean isActive;
    private Boolean isExpired;
    private Boolean isLocked;

    private Integer invalidAttemptCount;

    private LocalDateTime passwordUpdateDate;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLE" ,
            joinColumns = @JoinColumn(name = "USER_ID"),foreignKey = @ForeignKey(name = "FK_USER_ROLE_USER"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID") , inverseForeignKey = @ForeignKey(name = "FK_USER_ROLE_ROLE"))
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

}
