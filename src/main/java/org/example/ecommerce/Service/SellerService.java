package org.example.ecommerce.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.RegisterAddressDto;
import org.example.ecommerce.DTOS.Request.ResetPasswordDto;
import org.example.ecommerce.DTOS.Request.SellerDto;
import org.example.ecommerce.DTOS.Response.UpdateAddressDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.AdminSellerResponse;
import org.example.ecommerce.DTOS.Response.SellerProfileViewDto;
import org.example.ecommerce.Emails.EmailService;
import org.example.ecommerce.Entity.Address;
import org.example.ecommerce.Entity.Enum.RoleAuthority;
import org.example.ecommerce.Entity.Role;
import org.example.ecommerce.Entity.Seller;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.example.ecommerce.Repository.AddressRepository;
import org.example.ecommerce.Repository.RoleRepository;
import org.example.ecommerce.Repository.SellerRepository;
import org.example.ecommerce.Repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SellerService {
     UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
     EmailService sellerRegistrationEmail;
     SellerRepository sellerRepository;
     EmailService accountActivatedEmail;
    RoleRepository roleRepository;
    MessageSource messageSource;

   AddressRepository addressRepository;

    public BasicResponse registerSeller(SellerDto sellerDTO) {
        if(!sellerDTO.getPassword().equals(sellerDTO.getConfirmPassword())){
            throw new APIException("Password and Confirm Password Does not match", HttpStatus.BAD_REQUEST);
        }
        if (sellerRepository.existsByGst(sellerDTO.getGst())) {
            throw new APIException("GST already exists", HttpStatus.BAD_REQUEST);
        }
        if (sellerRepository.existsByCompanyName(sellerDTO.getCompanyName())) {
            throw new APIException("Company name already exists", HttpStatus.BAD_REQUEST);
        }
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

        RegisterAddressDto addressDTO = sellerDTO.getAddressesDTO();
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
            throw new APIException("Email Already Found", HttpStatus.BAD_REQUEST);
        }
        List<Role> roles = new ArrayList<>();
        Role sellerRole = new Role();
        sellerRole.setAuthority(RoleAuthority.SELLER);
        Role role = Optional.ofNullable(roleRepository.findByAuthority(RoleAuthority.SELLER)).orElse(sellerRole);
        roles.add(role);
        seller.setRoles(roles);
        seller.setPassword(bCryptPasswordEncoder.encode(seller.getPassword()));
        String email = seller.getEmail();
        sellerRegistrationEmail.sendEmail("Application Submitted",email,"Verification Token");
        userRepository.save(seller);
        return new BasicResponse("New Seller Registered Successfully Awaits Approval check email for more details", 200);
    }


    public BasicResponse activateSellerById(Long id, Locale locale) {
        Optional<Seller> sell = Optional.of(sellerRepository.findById(id).orElseThrow(
                ()-> new APIException("User with id is not found",HttpStatus.BAD_REQUEST)
        ));
        Seller seller = sell.get();

        if(seller.getIsActive()){
            String response = messageSource.getMessage("message.account.already.activated",null,locale);
            return new BasicResponse(response,200);
        }
        seller.setIsActive(true);
        String email = seller.getEmail();
        sellerRepository.save(seller);

        accountActivatedEmail.sendEmail("your Account activated please check", email, "Account Activated");

        String response = messageSource.getMessage("message.accountactivated",null,locale);
        return new BasicResponse(response,200);
    }

    public BasicResponse DeActivateSellerById(Long id,Locale locale) {
        Optional<Seller> sell = Optional.of(sellerRepository.findById(id).orElseThrow(
                ()-> new APIException("User with id is not found",HttpStatus.BAD_REQUEST)
        ));
        Seller seller = sell.get();

        if(!seller.getIsActive()){
            String response = messageSource.getMessage("message.account.already.deactivated",null,locale);
            return new BasicResponse(response,200);
        }
        seller.setIsActive(false);
        String email = seller.getEmail();
        sellerRepository.save(seller);

        accountActivatedEmail.sendEmail("your Account has been de-activated please check", email, "Account Activated");

        String response = messageSource.getMessage("message.accountdeactivated",null,locale);
        return new BasicResponse(response,200);

    }


    public BasicResponse updateMyPassword(ResetPasswordDto  resetPasswordDTO) {

        if(!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmPassword())){
            throw new APIException("password and confirm password should match", HttpStatus.BAD_REQUEST);
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = sellerRepository.findByEmail(email);
        seller.setPassword(bCryptPasswordEncoder.encode(resetPasswordDTO.getPassword()));
        seller.setPasswordUpdateDate(LocalDateTime.now());
        sellerRepository.save(seller);
        accountActivatedEmail.sendEmail("your password has been changed",email, "Account Password Changed");
        return new BasicResponse("Password Changed",200);
    }

    @Transactional
    public List<AdminSellerResponse> getAllSeller(Integer pageSize, Integer pageOffset, String sort, String email) {
        Page<Seller> pageOfSeller =
                sellerRepository.findAll(PageRequest.of(pageOffset, pageSize, Sort.by(sort)),email);
        List<Seller> sellerFromDatabase = pageOfSeller.getContent();
        List<AdminSellerResponse> sellers = sellerFromDatabase.stream()
                .map(seller -> {
                    AdminSellerResponse response = new AdminSellerResponse();
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
                        UpdateAddressDto address = new UpdateAddressDto();
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
                .toList();


        return sellers;

    }



    public SellerProfileViewDto getMyProfile() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        SellerProfileViewDto sellerProfileViewByHimselfDTO = new SellerProfileViewDto();
        Seller seller = sellerRepository.findByEmail(email);
        sellerProfileViewByHimselfDTO.setId(seller.getId());
        sellerProfileViewByHimselfDTO.setGst(seller.getGst());
        sellerProfileViewByHimselfDTO.setCompanyName(seller.getCompanyName());
        sellerProfileViewByHimselfDTO.setCompanyContact(seller.getCompanyContact());
        sellerProfileViewByHimselfDTO.setFirstName(seller.getFirstName());
        sellerProfileViewByHimselfDTO.setLastName(seller.getLastName());
        sellerProfileViewByHimselfDTO.setIsActive(seller.getIsActive());

        RegisterAddressDto addressDTO = new RegisterAddressDto();
        addressDTO.setId(seller.getAddresses().get(0).getId());
        addressDTO.setAddressLine(seller.getAddresses().get(0).getAddressLine());
        addressDTO.setLabel(seller.getAddresses().get(0).getLabel());
        addressDTO.setState(seller.getAddresses().get(0).getState());
        addressDTO.setCity(seller.getAddresses().get(0).getCity());
        addressDTO.setZipCode(seller.getAddresses().get(0).getZipCode());
        addressDTO.setCountry(seller.getAddresses().get(0).getCountry());

        sellerProfileViewByHimselfDTO.setAddress(addressDTO);

        String imageUrl = GetAndSaveImage.resolveImageUrl(seller.getId());
        sellerProfileViewByHimselfDTO.setImage(imageUrl);

        return sellerProfileViewByHimselfDTO;

    }

    public BasicResponse updateSellerAddress( Long id, String city, String state, String addressLine, String label, String country, String zipCode) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = sellerRepository.findByEmail(email);

        Address myAddress = addressRepository.findById(id).orElseThrow(
                ()-> new APIException("Address with id is not found",HttpStatus.BAD_REQUEST)
        );


        if(seller.getId()!=myAddress.getUser().getId()){
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
        return new BasicResponse("Address changed Successfully",200);
    }

    public BasicResponse updateMyProfile(String firstName, String lastName, String middleName, String gst, String companyName, String contact, MultipartFile image) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = Optional.ofNullable(sellerRepository.findByEmail(email)).orElseThrow(
                ()-> new APIException("User with email is not found",HttpStatus.BAD_REQUEST)
        );

        seller.setGst(Optional.ofNullable(gst).orElse(seller.getGst()));
        seller.setCompanyContact(Optional.ofNullable(contact).orElse(seller.getCompanyContact()));
        seller.setCompanyName(Optional.ofNullable(companyName).orElse(seller.getCompanyName()));
        seller.setFirstName(Optional.ofNullable(firstName).orElse(seller.getFirstName()));
        seller.setMiddleName(Optional.ofNullable(middleName).orElse(seller.getMiddleName()));
        seller.setLastName(Optional.ofNullable(lastName).orElse(seller.getLastName()));

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
            } catch (IOException e) {

            }
            Path imagePath = uploadPath.resolve(seller.getId() + extension);
            try {
                image.transferTo(imagePath.toFile());
            } catch (IOException e) {

            }
        }
        sellerRepository.save(seller);
        return new BasicResponse("Profile Updated",200);
    }

}
