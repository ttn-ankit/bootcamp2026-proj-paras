package org.example.ecommerce.Config;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.Entity.*;
import org.example.ecommerce.Entity.Enum.RoleAuthority;
import org.example.ecommerce.Repository.RoleRepository;
import org.example.ecommerce.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StartupData {

    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    RoleRepository roleRepository;

    @PostConstruct
    @Transactional
    public void init(){

        if(userRepository.findByEmail("admin@gmail.com")==null){
            User admin = new User();
            admin.setIsLocked(false);
            admin.setEmail("admin@gmail.com");
            admin.setPassword(bCryptPasswordEncoder.encode("Admin@123"));
            admin.setInvalidAttemptCount(0);
            admin.setFirstName("Paras");
            admin.setLastName("Kapoor");
            admin.setIsActive(true);
            admin.setIsExpired(false);
            admin.setIsDeleted(false);
            admin.setPasswordUpdateDate(LocalDateTime.now());
            List<Role> roles = new ArrayList<>();
            Role adminRole = new Role();
            adminRole.setAuthority(RoleAuthority.ADMIN);
            Role role = Optional.ofNullable(roleRepository.findByAuthority(RoleAuthority.ADMIN)).orElse(adminRole);
            roles.add(role);
            admin.setRoles(roles);
            userRepository.save(admin);
        }
}
}