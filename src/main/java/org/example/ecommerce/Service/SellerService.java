package org.example.ecommerce.Service;

import org.example.ecommerce.DTOS.Request.AddressDto;
import org.example.ecommerce.DTOS.Request.SellerDto;
import org.example.ecommerce.DTOS.Response.AddressResponse;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.SellerGetAllResponse;
import org.example.ecommerce.Emails.AccountActivated;
import org.example.ecommerce.Emails.SellerRegistration;
import org.example.ecommerce.Entity.Address;
import org.example.ecommerce.Entity.Role;
import org.example.ecommerce.Entity.Seller;
import org.example.ecommerce.GlobalExceptions.DuplicateEmailException;
import org.example.ecommerce.GlobalExceptions.UserNotFoundException;
import org.example.ecommerce.Repository.AddressRepository;
import org.example.ecommerce.Repository.RoleRepository;
import org.example.ecommerce.Repository.SellerRepository;
import org.example.ecommerce.Repository.UserRepository;
import org.example.ecommerce.Tokens.JwtLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SellerService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SellerRegistration sellerRegistrationEmail;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private AccountActivated accountActivatedEmail;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AddressRepository addressRepository;

    public String registerSeller(SellerDto sellerDTO) {

        Seller seller = new Seller();

        seller.setEmail(sellerDTO.getEmail());
        seller.setGst(sellerDTO.getGst());
        seller.setCompanyContact(sellerDTO.getCompanyContact());
        seller.setCompanyName(sellerDTO.getCompanyName());
        seller.setPassword(sellerDTO.getPassword());
        seller.setFirstName(sellerDTO.getFirstName());
        seller.setMiddleName(sellerDTO.getMiddleName());
        seller.setLastName(sellerDTO.getLastName());
        seller.setPasswordUpdateDate(LocalDateTime.now());
        seller.setIsActive(false);
        seller.setIsExpired(false);
        seller.setIsDeleted(false);
        seller.setIsLocked(false);
        seller.setInvalidAttemptCount(0);

        AddressDto addressDTO = sellerDTO.getAddressesDTO();
        Address sellerAddress = new Address();
        sellerAddress.setCity(addressDTO.getCity());
        sellerAddress.setAddressLine(addressDTO.getAddressLine());
        sellerAddress.setState(addressDTO.getState());
        sellerAddress.setCountry(addressDTO.getCountry());
        sellerAddress.setZipCode(addressDTO.getZipCode());
        sellerAddress.setLabel(addressDTO.getLabel());
        sellerAddress.setUser(seller);
        seller.setAddresses(Arrays.asList(sellerAddress));

        if(userRepository.existsByEmail(seller.getEmail())){
            throw new DuplicateEmailException("Email Already Found");
        }
        List<Role> roles = new ArrayList<>();
        Role sellerRole = new Role();
        sellerRole.setAuthority("SELLER");
        Role role = Optional.ofNullable(roleRepository.findByAuthority("SELLER")).orElse(sellerRole);
        roles.add(role);
        seller.setRoles(roles);
        seller.setPassword(bCryptPasswordEncoder.encode(seller.getPassword()));
        String email = seller.getEmail();
//        String token  = JwtRegistrationToken.generateRegistrationToken(email);
        sellerRegistrationEmail.sendRegistrationStatusEmail(email);
        Seller success =  userRepository.save(seller);
        if(success==null){
            return "could not Register Seller";
        }
        return "New Seller Registered Successfully Awaits Approval check email for more details";
    }


    public BasicResponse activateSellerById(Long id, Locale locale) {
        Optional<Seller> sell = Optional.of(sellerRepository.findById(id).orElseThrow(
                ()-> new UserNotFoundException("User with id is not found")
        ));
        Seller seller = sell.get();

        if(seller.getIsActive()){
            String response = messageSource.getMessage("message.account.already.activated",null,locale);
            return new BasicResponse(response,true);
        }
        seller.setIsActive(true);
        String email = seller.getEmail();
        sellerRepository.save(seller);

        accountActivatedEmail.sendAccountActivatedEmail("your Account activated please check", email);

        String response = messageSource.getMessage("message.accountactivated",null,locale);
        return new BasicResponse(response,true);
    }

    public BasicResponse DeActivateSellerById(Long id,Locale locale) {
        Optional<Seller> sell = Optional.of(sellerRepository.findById(id).orElseThrow(
                ()-> new UserNotFoundException("User with id is not found")
        ));
        Seller seller = sell.get();

        if(!seller.getIsActive()){
            String response = messageSource.getMessage("message.account.already.deactivated",null,locale);
            return new BasicResponse(response,true);
        }
        seller.setIsActive(false);
        String email = seller.getEmail();
        sellerRepository.save(seller);

        accountActivatedEmail.sendAccountActivatedEmail("your Account has been de-activated please check", email);

        String response = messageSource.getMessage("message.accountdeactivated",null,locale);
        return new BasicResponse(response,true);

    }


    public void updateMyPassword(String token,String password) {
        String email = JwtLogin.validateLoginAccessToken(token);
        Seller seller = sellerRepository.findByEmail(email);
        seller.setPassword(bCryptPasswordEncoder.encode(password));
        seller.setPasswordUpdateDate(LocalDateTime.now());
        sellerRepository.save(seller);
        accountActivatedEmail.sendAccountPasswordChangedEmail("your password has been changed",email);
    }

    @Transactional
    public List<SellerGetAllResponse> getAllSeller(Integer pageSize, Integer pageOffset, String sort, String email) {
        Page<Seller> pageOfSeller =
                sellerRepository.findAll(PageRequest.of(pageOffset, pageSize, Sort.by(sort)),email);
        List<Seller> sellerFromDatabase = pageOfSeller.getContent();
        List<SellerGetAllResponse> sellers = sellerFromDatabase.stream()
                .map(seller -> {
                    SellerGetAllResponse response = new SellerGetAllResponse();
                    response.setId(seller.getId());
                    response.setContact(seller.getCompanyContact());
                    response.setFullName(
                            Optional.ofNullable(seller.getFirstName()).orElse("") + " " +
                                    Optional.ofNullable(seller.getMiddleName()).orElse("") + " " +
                                    Optional.ofNullable(seller.getLastName()).orElse("")
                    );
                    response.setEmail(seller.getEmail());
                    response.setCompanyName(seller.getCompanyName());
                    response.setIsActive(seller.getIsActive());

                    List<Address> addresses = seller.getAddresses();

                    if (addresses != null && !addresses.isEmpty()) {
                        Address sellerAddress = addresses.get(0);
                        AddressResponse address = new AddressResponse();
                        address.setZipCode(sellerAddress.getZipCode());
                        address.setAddressLine(sellerAddress.getAddressLine());
                        address.setCity(sellerAddress.getCity());
                        address.setCountry(sellerAddress.getCountry());
                        address.setState(sellerAddress.getState());
                        address.setLabel(sellerAddress.getLabel());
                        address.setId(sellerAddress.getId());
                        response.setCompanyAddress(address);

                    }

                    return response;
                })
                .collect(Collectors.toUnmodifiableList());


        return sellers;

    }


}
