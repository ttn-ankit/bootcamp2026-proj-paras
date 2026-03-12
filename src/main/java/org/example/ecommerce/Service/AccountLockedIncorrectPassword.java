package org.example.ecommerce.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.Emails.EmailService;
import org.example.ecommerce.Entity.User;
import org.example.ecommerce.GlobalExceptions.InvalidEmail;
import org.example.ecommerce.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountLockedIncorrectPassword {
    UserRepository userRepository;
    EmailService accountLocked;


    public void passwordIncorrectCountIncrease(String email){
        User user = userRepository.findByEmail(email);
        Integer invalidCount = Optional.ofNullable(user.getInvalidAttemptCount()).orElse(0);
        if(invalidCount>=2){
            user.setIsLocked(true);
            accountLocked.sendEmail("Your Account has been Locked.", email," Account Locked");
            userRepository.save(user);
            throw new InvalidEmail("Account is locked due to max number of attempts");

        }
        else{
            user.setInvalidAttemptCount(++invalidCount);
        }
        userRepository.save(user);
    }
}
