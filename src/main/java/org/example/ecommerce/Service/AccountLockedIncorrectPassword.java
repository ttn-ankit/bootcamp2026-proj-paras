package org.example.ecommerce.Service;

import org.example.ecommerce.Emails.AccountLocked;
import org.example.ecommerce.Entity.User;
import org.example.ecommerce.GlobalExceptions.InvalidEmail;
import org.example.ecommerce.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountLockedIncorrectPassword {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountLocked accountLocked;


    public void passwordIncorrectCountIncrease(String email){
        User user = userRepository.findByEmail(email);
        Integer invalidCount = Optional.ofNullable(user.getInvalidAttemptCount()).orElse(0);
        if(invalidCount>=2){
            user.setIsLocked(true);
            accountLocked.sendAccountLockedEmail(email);
            userRepository.save(user);
            throw new InvalidEmail("Account is locked due to max number of attempts");

        }
        else{
            user.setInvalidAttemptCount(++invalidCount);
        }
        userRepository.save(user);
    }
}
