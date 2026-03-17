package org.example.ecommerce.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.Entity.Enum.RoleAuthority;
import org.example.ecommerce.Entity.User;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.example.ecommerce.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsService implements UserDetailsService {
     UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email);
        if (user==null){
            throw new APIException("Incorrect Email. give correct email address associated with account", HttpStatus.BAD_REQUEST);
        }
        RoleAuthority roleOfUser = user.getRoles().get(0).getAuthority();
        if(roleOfUser.equals(RoleAuthority.CUSTOMER) ||  roleOfUser.equals(RoleAuthority.SELLER)){
            if(user.getIsExpired()){
                throw new APIException("Your Password is expired please reset is using forget password",HttpStatus.BAD_REQUEST);
            }
            LocalDateTime lastPasswordUpdateDate = user.getPasswordUpdateDate();
            if(lastPasswordUpdateDate.plusDays(30).isBefore(LocalDateTime.now())){
                throw new APIException("Password is older more than 30 days please update it first",HttpStatus.BAD_REQUEST);
            }
        }
        if(user.getIsLocked()){

            throw new APIException("Account is Locked Due to Maximum number of attempts.",HttpStatus.BAD_REQUEST);
        }
        if(!user.getIsActive()){
            if(roleOfUser.equals("CUSTOMER")){
                throw new APIException("Account is not active Please request " +
                        "new Activation token and get your activated",HttpStatus.BAD_REQUEST);
            }
            else{
                throw new APIException("Account is not active yet Admin will review it soon",HttpStatus.BAD_REQUEST);
            }
        }
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(String.valueOf(roleOfUser))
                .disabled(user.getIsLocked())
                .build();
    }

    public void setInvalidCountTo(int i,String email) {
        User user = userRepository.findByEmail(email);
        user.setInvalidAttemptCount(i);
        user.setIsLocked(false);
        userRepository.save(user);
    }
}
