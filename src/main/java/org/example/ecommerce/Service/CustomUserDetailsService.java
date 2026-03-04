package org.example.ecommerce.Service;

import jakarta.transaction.Transactional;
import org.example.ecommerce.Emails.AccountLocked;
import org.example.ecommerce.Entity.User;
import org.example.ecommerce.GlobalExceptions.AccountNotActiveException;
import org.example.ecommerce.GlobalExceptions.InvalidEmail;
import org.example.ecommerce.GlobalExceptions.PasswordDoesNotMatchException;
import org.example.ecommerce.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountLocked accountLocked;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email);
        if (user==null){
            throw new InvalidEmail("Incorrect Email. give correct email address associated with account");
        }
        String roleOfUser = user.getRoles().get(0).getAuthority();
        if(roleOfUser.equals("CUSTOMER") ||  roleOfUser.equals("SELLER")){
            if(user.getIsExpired()){
                throw new AccountNotActiveException("Your Password is expired please reset is using forget password");
            }
            LocalDateTime lastPasswordUpdateDate = user.getPasswordUpdateDate();
            if(lastPasswordUpdateDate.plusDays(30).isBefore(LocalDateTime.now())){
                throw new PasswordDoesNotMatchException("Password is older more than 30 days please update it first");
            }
        }
        if(user.getIsLocked()){

            throw new InvalidEmail("Account is Locked Due to Maximum number of attempts.");
        }
        if(!user.getIsActive()){
            if(roleOfUser.equals("CUSTOMER")){
                throw new AccountNotActiveException("Account is not active Please request " +
                        "new Activation token and get your activated");
            }
            else{
                throw new AccountNotActiveException("Account is not active yet Admin will review it soon");
            }
        }
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(roleOfUser)
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
