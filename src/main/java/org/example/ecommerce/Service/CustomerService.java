package org.example.ecommerce.Service;


import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.CustomerDto;
import org.example.ecommerce.DTOS.Response.UpdateAddressDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.AdminCustomerResponse;
import org.example.ecommerce.DTOS.Response.CustomerProfileViewDto;
import org.example.ecommerce.Emails.EmailService;
import org.example.ecommerce.Entity.*;
import org.example.ecommerce.Entity.Enum.RoleAuthority;
import org.example.ecommerce.GlobalExceptions.*;
import org.example.ecommerce.Repository.*;
import org.example.ecommerce.Security.JWTService;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

    UserRepository userRepository;
     BCryptPasswordEncoder bCryptPasswordEncoder;
     EmailService customerRegistration;
     ActivationTokenRepository activationTokenRepo;
   CustomerRepository customerRepository;
     EmailService accountActivatedEmail;
   RoleRepository roleRepository;
     AddressRepository addressRepository;
     MessageSource messageSource;
     JWTService jwtService;

    public BasicResponse registerCustomer(CustomerDto dto) {
        if(!dto.getPassword().equals(dto.getConfirmPassword())){
            throw new APIException("Password and Confirm Password Does not match", HttpStatus.BAD_REQUEST);
        }
        Customer customer = new Customer();

        customer.setFirstName(dto.getFirstName());
        customer.setMiddleName(dto.getMiddleName());
        customer.setLastName(dto.getLastName());
        customer.setContact(dto.getContact());
        customer.setEmail(dto.getEmail());
        customer.setPassword(dto.getPassword());
        customer.setPasswordUpdateDate(LocalDateTime.now());
        customer.setIsActive(false);
        customer.setIsExpired(false);
        customer.setIsDeleted(false);
        customer.setIsLocked(false);
        customer.setInvalidAttemptCount(0);
            List<Address> customerAddresses = dto.getAddressesDTO().stream()
                    .map(
                            addressDTO -> {
                                Address address = new Address();
                                address.setCity(addressDTO.getCity());
                                address.setAddressLine(addressDTO.getAddressLine());
                                address.setState(addressDTO.getState());
                                address.setCountry(addressDTO.getCountry());
                                address.setZipCode(addressDTO.getZipCode());
                                address.setLabel(addressDTO.getLabel());
                                address.setUser(customer);
                                return address;
                            }
                    )
                    .collect(Collectors.toList());

            customer.setAddresses(customerAddresses);

        if(userRepository.existsByEmail(customer.getEmail())){
            throw new APIException("Email Already Found", HttpStatus.BAD_REQUEST);
        }
        Role customerRole = new Role();
        customerRole.setAuthority(RoleAuthority.CUSTOMER);
        Role role = Optional.ofNullable(roleRepository.findByAuthority(RoleAuthority.CUSTOMER)).orElse(customerRole);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        customer.setRoles(roles);
        customer.setLastName(dto.getLastName());
        customer.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
        String email = customer.getEmail();
        String token  = jwtService.generateAccessToken(email);
        activationTokenRepo.save(new UserActivationToken(email,token));
        customerRegistration.sendEmail(token,email,"Verification Token");
        userRepository.save(customer);
        return new BasicResponse("New Customer Registered Successfully", 200);

    }

    public BasicResponse activateCustomer(String token, Locale locale) {
        String email = jwtService.extractUsername(token);
        User customer = userRepository.findByEmail(email);
        UserActivationToken userActivationToken = activationTokenRepo.findByEmail(email);
        if(customer!=null){
            if(customer.getIsActive()){
                String response = messageSource.getMessage("message.account.activated.active",null,locale);
                return new BasicResponse(response,200);
            }
            if(userActivationToken==null){
                token  = jwtService.generateAccessToken(email);
                activationTokenRepo.save(new UserActivationToken(email,token));
                customerRegistration.sendEmail(token,email,"Verification Token");
                String response = messageSource.getMessage("message.account.activated.null.resend",null,locale);
                return new BasicResponse(response,200);
            }
            if(!userActivationToken.getToken().equals(token)){
                token  = jwtService.generateAccessToken(email);
                activationTokenRepo.save(new UserActivationToken(email,token));
                customerRegistration.sendEmail(token,email,"Verification Token");

                String response = messageSource.getMessage("message.account.activated.resend",null,locale);
                return new BasicResponse(response,200);
            }
            activationTokenRepo.delete(userActivationToken);
            customer.setIsActive(true);
            userRepository.save(customer);
            String response = messageSource.getMessage("message.account.activated",null,locale);
            return new BasicResponse(response,200);
        }

        String response = messageSource.getMessage("message.account.not.activated",null,locale);
        return new BasicResponse(response,200);
    }

    public BasicResponse reSendActivationLink(String email,Locale locale) {
        User customer = userRepository.findByEmail(email);
        if(customer==null){
            throw new APIException("User With "+ email +" is not found", HttpStatus.BAD_REQUEST);
        }
        if(customer.getIsActive()){

            String response = messageSource.getMessage("message.account.activated.active",null,locale);
            return new BasicResponse(response,200);
        }
        RoleAuthority role = customer.getRoles().get(0).getAuthority();
        if(role.equals(RoleAuthority.CUSTOMER)){
            String token  = jwtService.generateAccessToken(email);
            activationTokenRepo.save(new UserActivationToken(email,token));
            customerRegistration.sendEmail(token,email,"Verification Token");

            String response = messageSource.getMessage("message.account.activated.link.send",null,locale);
            return new BasicResponse(response,200);
        }

        String response = messageSource.getMessage("message.account.activated.by.admin",null,locale);
        return new BasicResponse(response,200);
    }

    @Transactional
    public BasicResponse activateCustomerById(Long id, Locale locale) {
        Optional<Customer> cust = Optional.of(customerRepository.findById(id).orElseThrow(
                ()-> new APIException("User with id is not found",HttpStatus.BAD_REQUEST)
        ));
        Customer customer = cust.get();


        if(customer.getIsActive()){
            String response = messageSource.getMessage("message.account.already.activated",null,locale);
            return new BasicResponse(response,200);
        }
        customer.setIsActive(true);
        String email = customer.getEmail();
        customerRepository.save(customer);

        accountActivatedEmail.sendEmail("your Account activated please check", email,"Activation Mail");
        String response = messageSource.getMessage("message.accountactivated",null,locale);
        return new BasicResponse(response,200);

    }

    public BasicResponse deActivateCustomerById(Long id,Locale locale) {

        Optional<Customer> cust = Optional.of(customerRepository.findById(id).orElseThrow(
                ()-> new APIException("User with id is not found",HttpStatus.BAD_REQUEST)
        ));
        Customer customer = cust.get();

        if(!customer.getIsActive()){
            String response = messageSource.getMessage("message.account.already.deactivated",null,locale);
            return new BasicResponse(response,200);
        }
        customer.setIsActive(false);
        String email = customer.getEmail();
        customerRepository.save(customer);

        accountActivatedEmail.sendEmail("your Account has been de-activated please check",email,"Deactivation Email");

        String response = messageSource.getMessage("message.accountdeactivated",null,locale);
        return new BasicResponse(response,200);

    }

    public BasicResponse updateProfilePassword(String password, String confirmPassword) {

        if(!password.equals(confirmPassword)){
            throw new APIException("Passwords do not match.", HttpStatus.BAD_REQUEST);
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email);
        customer.setPassword(bCryptPasswordEncoder.encode(password));
        customer.setPasswordUpdateDate(LocalDateTime.now());
        customerRepository.save(customer);
        accountActivatedEmail.sendEmail("your password has been changed", email, "Account Password Changed");
        return new BasicResponse("Password Changed",200);

    }

    public BasicResponse addNewCustomerAddress(UpdateAddressDto address) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email);
        List<Address> addresses = customer.getAddresses();
        Address address1 = new Address();
        address1.setAddressLine(address.getAddressLine());
        address1.setState(address.getState());
        address1.setCity(address.getCity());
        address1.setCountry(address.getCountry());
        address1.setZipCode(address.getZipCode());
        address1.setLabel(address.getLabel());
        address1.setUser(customer);

        addresses.add(address1);


        customerRepository.save(customer);
        return new BasicResponse("Address saved",200);
    }

    public BasicResponse deletedThisAddress(Long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email);

        Address myAddress = addressRepository.findById(id).orElseThrow(
                ()-> new APIException("Address with this id is not found ",HttpStatus.BAD_REQUEST)
        );


        if(!customer.getId().equals(myAddress.getUser().getId())){
            throw new APIException("Not your address you can not delete this",HttpStatus.BAD_REQUEST);
        }
        addressRepository.delete(myAddress);
        return new BasicResponse("Address deleted",200);
    }

    public List<AdminCustomerResponse> getAllCustomer(Integer pageSize, Integer pageOffset, String sort, String email) {

        Page<Customer> pageOfCustomer =
                customerRepository.findAll(PageRequest.of(pageOffset,pageSize, Sort.by(sort).descending()),email);

        List<Customer> customersFromDatabase = pageOfCustomer.getContent();

        List<AdminCustomerResponse> customers = customersFromDatabase.stream()
                .map(customer -> {
                    AdminCustomerResponse response = new AdminCustomerResponse();
                    response.setId(customer.getId());
                    response.setFullName((
                            Optional.ofNullable(customer.getFirstName()).orElse("")+" "+
                                    Optional.ofNullable(customer.getMiddleName()).orElse("")+" "+
                                    Optional.ofNullable(customer.getLastName()).orElse("")));
                    response.setIsActive(customer.getIsActive());
                    response.setEmail(customer.getEmail());
                    return response;
                }).toList();

        return customers;
    }


    public CustomerProfileViewDto getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email);
        CustomerProfileViewDto customerProfile = new CustomerProfileViewDto();
        customerProfile.setId(customer.getId());
        customerProfile.setFirstName(customer.getFirstName());
        customerProfile.setLastName(customer.getLastName());
        customerProfile.setContact(customer.getContact());
        customerProfile.setIsActive(customer.getIsActive());
        String imageUrl = GetAndSaveImage.resolveImageUrl(customer.getId());
        customerProfile.setImage(imageUrl);
        return customerProfile;

    }

    public List<UpdateAddressDto> getAllCustomerAddress() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email);
        List<UpdateAddressDto> customerAddresses;
            customerAddresses = customer.getAddresses().stream()
                    .map(
                            address -> {
                                UpdateAddressDto addressDTO = new UpdateAddressDto();
                                addressDTO.setCity(address.getCity());
                                addressDTO.setState(address.getState());
                                addressDTO.setCountry(address.getCountry());
                                addressDTO.setLabel(address.getLabel());
                                addressDTO.setZipCode(address.getZipCode());
                                addressDTO.setAddressLine(address.getAddressLine());
                                addressDTO.setId(address.getId());
                                return addressDTO;
                            }
                    )
                    .toList();

        return customerAddresses;
    }

    public BasicResponse updateCustomerProfileFields(String firstName, String lastName, String middleName, String contact, MultipartFile image) {


        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email);

        if(firstName!=null) customer.setFirstName(firstName);
        if(lastName!=null) customer.setLastName(lastName);
        if(middleName!=null) customer.setMiddleName(middleName);
        if(contact!=null) customer.setContact(contact);

        if (image != null && !image.isEmpty()) {
            String originalFilename = image.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            List<String> allowedExtensions = List.of(".jpg", ".jpeg", ".png", ".bmp");
            if (!allowedExtensions.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("Invalid file type. Only JPG, JPEG, PNG, and WEBP are allowed.");
            }

            String uploadFolder = "images/user";
            Path uploadPath = Paths.get(uploadFolder).toAbsolutePath();
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException ignored) {

            }
            Path imagePath = uploadPath.resolve(customer.getId() + extension);
            try {
                image.transferTo(imagePath.toFile());
            } catch (IOException ignored) {
            }
        }

        customerRepository.save(customer);
        return new BasicResponse("Profile updated successfully",200);
    }

    public BasicResponse updateAddress(Long id, String city, String state, String addressLine, String label, String country, String zipCode) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email);

        Address myAddress = addressRepository.findById(id).orElseThrow(
                ()-> new APIException("Address with this id is not found ", HttpStatus.BAD_REQUEST)
        );


        if(!customer.getId().equals(myAddress.getUser().getId())){
            throw new APIException("Not your address you can not update this",HttpStatus.BAD_REQUEST);
        }

        addressLine = Optional.ofNullable(addressLine).orElse(myAddress.getAddressLine());
        city = Optional.ofNullable(city).orElse(myAddress.getCity());
        state = Optional.ofNullable(state).orElse(myAddress.getState());
        label = Optional.ofNullable(label).orElse(myAddress.getLabel());
        zipCode = Optional.ofNullable(zipCode).orElse(myAddress.getZipCode());
        country = Optional.ofNullable(country).orElse(myAddress.getCountry());

        myAddress.setAddressLine(addressLine);
        myAddress.setCity(city);
        myAddress.setState(state);
        myAddress.setLabel(label);
        myAddress.setZipCode(zipCode);
        myAddress.setCountry(country);

        addressRepository.save(myAddress);

        return new BasicResponse("Address Updated",200);

    }



}
