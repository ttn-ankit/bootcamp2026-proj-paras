package org.example.ecommerce.Service;


import jakarta.transaction.Transactional;
import org.example.ecommerce.DTOS.Request.CustomerDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.Emails.AccountActivated;
import org.example.ecommerce.Emails.CustomerRegistration;
import org.example.ecommerce.Entity.*;
import org.example.ecommerce.GlobalExceptions.DuplicateEmailException;
import org.example.ecommerce.GlobalExceptions.InvalidEmail;
import org.example.ecommerce.GlobalExceptions.UserNotFoundException;
import org.example.ecommerce.Repository.*;
import org.example.ecommerce.Tokens.JwtLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private CustomerRegistration customerRegistration;
    @Autowired
    private ActivationTokenRepository activationTokenRepo;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountActivated accountActivatedEmail;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private MessageSource messageSource;

    public String registerCustomer(CustomerDto dto) {
        Customer customer = new Customer();

        customer.setFirstName(dto.getFirstName());
        customer.setMiddleName(dto.getMiddleName());
        customer.setLastName(dto.getMiddleName());
        customer.setContact(dto.getContact());
        customer.setEmail(dto.getEmail());
        customer.setPassword(dto.getPassword());
        customer.setPasswordUpdateDate(LocalDateTime.now());
        customer.setIsActive(false);
        customer.setIsExpired(false);
        customer.setIsDeleted(false);
        customer.setIsLocked(false);
        customer.setInvalidAttemptCount(0);

        try {
            List<Address> sellerAddresses = dto.getAddressesDTO().stream()
                    .map(
                            addressDTO -> {
                                Address address = new Address();
                                address.setCity(addressDTO.getCity());
                                address.setAddressLine(addressDTO.getAddressLine());
                                address.setState(addressDTO.getState());
                                address.setCountry(address.getCountry());
                                address.setZipCode(addressDTO.getZipCode());
                                address.setLabel(addressDTO.getLabel());
                                address.setUser(customer);
                                return address;
                            }
                    )
                    .collect(Collectors.toList());

            customer.setAddresses(sellerAddresses);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(userRepository.existsByEmail(customer.getEmail())){
            throw new DuplicateEmailException("Email Already Found");
        }
        Role customerRole = new Role();
        customerRole.setAuthority("CUSTOMER");
        Role role = Optional.ofNullable(roleRepository.findByAuthority("CUSTOMER")).orElse(customerRole);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        customer.setRoles(roles);
        customer.setLastName(dto.getLastName());
        customer.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
        String email = customer.getEmail();
        String token  = JwtLogin.generateLoginAccessToken(email);
        activationTokenRepo.save(new UserActivationToken(email,token));
        customerRegistration.sendVerificationEmail(email,token);
        Customer success =  userRepository.save(customer);
        if(success==null){
            return "could not Register Customer";
        }
        return "New Customer Registered Successfully";
    }

    public BasicResponse activateCustomer(String token, Locale locale) {
        String email = JwtLogin.validateLoginAccessToken(token);
        User customer = userRepository.findByEmail(email);
        UserActivationToken userActivationToken = activationTokenRepo.findByEmail(email);
        if(customer!=null){
            if(customer.getIsActive()){
                String response = messageSource.getMessage("message.account.activated.active",null,locale);
                return new BasicResponse(response,true);
            }
            if(userActivationToken==null){
                token  = JwtLogin.generateLoginAccessToken(email);
                activationTokenRepo.save(new UserActivationToken(email,token));
                customerRegistration.sendVerificationEmail(email,token);
                String response = messageSource.getMessage("message.account.activated.null.resend",null,locale);
                return new BasicResponse(response,true);
            }
            if(!userActivationToken.getToken().equals(token)){
                token  = JwtLogin.generateLoginAccessToken(email);
                activationTokenRepo.save(new UserActivationToken(email,token));
                customerRegistration.sendVerificationEmail(email,token);

                String response = messageSource.getMessage("message.account.activated.resend",null,locale);
                return new BasicResponse(response,true);
            }
            activationTokenRepo.delete(userActivationToken);
            customer.setIsActive(true);
            userRepository.save(customer);
            String response = messageSource.getMessage("message.account.activated",null,locale);
            return new BasicResponse(response,true);
        }

        String response = messageSource.getMessage("message.account.not.activated",null,locale);
        return new BasicResponse(response,true);
    }

    public BasicResponse reSendActivationLink(String email,Locale locale) {
        User customer = userRepository.findByEmail(email);
        if(customer==null){
            throw new InvalidEmail("User With "+ email +" is not found");
        }
        if(customer.getIsActive()){

            String response = messageSource.getMessage("message.account.activated.active",null,locale);
            return new BasicResponse(response,true);
        }
        String role = customer.getRoles().get(0).getAuthority();
        if(role.equals("CUSTOMER")){
            String token  = JwtLogin.generateLoginAccessToken(email);
            activationTokenRepo.save(new UserActivationToken(email,token));
            customerRegistration.sendVerificationEmail(email,token);

            String response = messageSource.getMessage("message.account.activated.link.send",null,locale);
            return new BasicResponse(response,true);
        }

        String response = messageSource.getMessage("message.account.activated.by.admin",null,locale);
        return new BasicResponse(response,true);
    }

    @Transactional
    public BasicResponse activateCustomerById(Long id, Locale locale) {
        Optional<Customer> cust = Optional.of(customerRepository.findById(id).orElseThrow(
                ()-> new UserNotFoundException("User with id is not found")
        ));
        Customer customer = cust.get();


        if(customer.getIsActive()){
            String response = messageSource.getMessage("message.account.already.activated",null,locale);
            return new BasicResponse(response,true);
        }
        customer.setIsActive(true);
        String email = customer.getEmail();
        customerRepository.save(customer);

        accountActivatedEmail.sendAccountActivatedEmail("your Account activated please check", email);
        String response = messageSource.getMessage("message.accountactivated",null,locale);
        return new BasicResponse(response,true);

    }

    public BasicResponse deActivateCustomerById(Long id,Locale locale) {

        Optional<Customer> cust = Optional.of(customerRepository.findById(id).orElseThrow(
                ()-> new UserNotFoundException("User with id is not found")
        ));
        Customer customer = cust.get();

        if(!customer.getIsActive()){
            String response = messageSource.getMessage("message.account.already.deactivated",null,locale);
            return new BasicResponse(response,true);
        }
        customer.setIsActive(false);
        String email = customer.getEmail();
        customerRepository.save(customer);

        accountActivatedEmail.sendAccountActivatedEmail("your Account has been de-activated please check",email);

        String response = messageSource.getMessage("message.accountdeactivated",null,locale);
        return new BasicResponse(response,true);

    }

    public void updateProfilePassword(String token, String password) {

        String email = JwtLogin.validateLoginAccessToken(token);
        Customer customer = customerRepository.findByEmail(email);
        customer.setPassword(bCryptPasswordEncoder.encode(password));
        customer.setPasswordUpdateDate(LocalDateTime.now());
        customerRepository.save(customer);
        accountActivatedEmail.sendAccountPasswordChangedEmail("your password has been changed", email);

    }

    }
