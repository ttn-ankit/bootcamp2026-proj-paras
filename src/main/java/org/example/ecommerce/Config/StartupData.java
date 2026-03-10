package org.example.ecommerce.Config;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.Entity.*;
import org.example.ecommerce.Repository.RoleRepository;
import org.example.ecommerce.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
            adminRole.setAuthority("ADMIN");
            Role role = Optional.ofNullable(roleRepository.findByAuthority("ADMIN")).orElse(adminRole);
            roles.add(role);
            admin.setRoles(roles);
            userRepository.save(admin);
        }

        if(userRepository.findByEmail("customer@gmail.com")==null){
            Customer customer = new Customer();
            customer.setEmail("customer@gmail.com");
            customer.setFirstName("customer");
            customer.setContact("9000998887");
            customer.setPassword(bCryptPasswordEncoder.encode("Customer@123"));
            customer.setIsActive(false);
            customer.setIsExpired(false);
            customer.setIsDeleted(false);
            customer.setIsLocked(false);
            customer.setInvalidAttemptCount(0);
            customer.setPasswordUpdateDate(LocalDateTime.now());

            List<Role> roles = new ArrayList<>();
            Role customerRole = new Role();
            customerRole.setAuthority("CUSTOMER");
            Role role = Optional.ofNullable(roleRepository.findByAuthority("CUSTOMER")).orElse(customerRole);
            roles.add(role);
            customer.setRoles(roles);

            Address address1 = new Address();
            address1.setCity("New Delhi");
            address1.setState("Delhi");
            address1.setCountry("India");
            address1.setAddressLine("221B Rajendra Nagar");
            address1.setZipCode(110060);
            address1.setLabel("Home");
            address1.setUser(customer);

            Address address2 = new Address();
            address2.setCity("Bangalore");
            address2.setState("Karnataka");
            address2.setCountry("India");
            address2.setAddressLine("5th Floor, Manyata Tech Park, Nagavara");
            address2.setZipCode(560045);
            address2.setLabel("Office");
            address2.setUser(customer);

            List<Address> addresses = new ArrayList<>();
            addresses.add(address1);
            addresses.add(address2);
            customer.setAddresses(addresses);

            userRepository.save(customer);
        }

        if(userRepository.findByEmail("seller@gmail.com")==null){

            Seller seller = new Seller();
            seller.setEmail("seller@gmail.com");
            seller.setPassword(bCryptPasswordEncoder.encode("Seller@123"));
            seller.setFirstName("seller");
            seller.setLastName("last name seller");
            seller.setGst("1q2w3eRGrd4e5r3");
            seller.setCompanyName("Ptn");
            seller.setCompanyContact("8767451029");
            seller.setIsActive(false);
            seller.setIsExpired(false);
            seller.setIsDeleted(false);
            seller.setInvalidAttemptCount(0);
            seller.setPasswordUpdateDate(LocalDateTime.now());
            seller.setIsLocked(false);

            List<Role> roles = new ArrayList<>();
            Role sellerRole = new Role();
            sellerRole.setAuthority("SELLER");
            Role role = Optional.ofNullable(roleRepository.findByAuthority("SELLER")).orElse(sellerRole);
            roles.add(role);
            seller.setRoles(roles);


            Address address = new Address();
            address.setCity("New Delhi");
            address.setState("New Delhi");
            address.setCountry("India");
            address.setAddressLine("221B");
            address.setZipCode(234512);
            address.setLabel("OFFICE");
            address.setUser(seller);

            List<Address> addresses = Arrays.asList(address);
            seller.setAddresses(addresses);

            userRepository.save(seller);



        }
}
}