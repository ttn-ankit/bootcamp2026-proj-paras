package org.example.ecommerce.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.Emails.EmailService;
import org.example.ecommerce.Entity.ForgetPasswordToken;
import org.example.ecommerce.Entity.User;
import org.example.ecommerce.GlobalExceptions.*;
import org.example.ecommerce.Repository.ForgetPasswordRepository;
import org.example.ecommerce.Repository.UserRepository;
import org.example.ecommerce.Tokens.JwtForgot;
import org.example.ecommerce.Tokens.JwtLogin;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ForgotPasswordService {

     UserRepository userRepository;
     BCryptPasswordEncoder passwordEncoder;
     ForgetPasswordRepository forgetPasswordTokenRepo;
     EmailService forgotPasswordEmail;

    public void processForgotPassword(String email){

        validateEmail(email);

        User user = userRepository.findByEmail(email);

        if(user != null){

            if(user.getIsLocked()){
                throw new AccountNotActiveException(
                        "Account is locked. You can not reset password. contact Admin"
                );
            }

            if(!user.getIsActive()){
                throw new AccountNotActiveException(
                        "Account is not active please contact admin or activate it by activation link"
                );
            }

            String token = JwtForgot.generateForgetPasswordToken(email);

            setForgetTokenInDataBase(email, token);

            forgotPasswordEmail.sendEmail(token,email,"Reset Token");
        }
    }
    private void validateEmail(String email){

        if(email == null || email.trim().isEmpty()){
            throw new InvalidEmail("email is not valid");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if(!email.matches(emailRegex)){
            throw new InvalidEmail("email is not valid");
        }
    }

    public User getForgetPasswordTokenUser(String email) {
        return userRepository.findByEmail(email);

    }
    @Transactional
    public void setPassword(String token, String password) {
        String claim = JwtForgot.validateForgetPasswordToken(token);
        if(!claim.equals("password-reset")){
            throw new InvalidJwtToken("Invalid forget Password Token, request another");
        }
        String email = JwtLogin.validateLoginAccessToken(token);
        ForgetPasswordToken forgetToken = forgetPasswordTokenRepo.findByEmail(email);
        if(!token.equals(forgetToken.getForgetToken())){
            throw new InvalidJwtToken("The Request Token is Changed. Please use latest One.");
        }
        User user = userRepository.findByEmail(email);
        if(user==null){
            throw new UserNotFoundException("User Not Found with email: "+email);
        }
        if(!user.getIsActive()){
            throw new AccountNotActiveException("Activate you account first");
        }
        if(user.getIsLocked()){
            throw new NotPermitted("Your Account is locked. Contact admin to unlock your account");
        }
        String userPassword = passwordEncoder.encode(password);
        user.setPassword(userPassword);
        user.setInvalidAttemptCount(0);
        user.setPasswordUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        forgetPasswordTokenRepo.deleteById(email);
    }

    public void setForgetTokenInDataBase(String email, String token) {
        forgetPasswordTokenRepo.save(new ForgetPasswordToken(email,token));
    }

    public void removeToken(String token) {
        String email = JwtForgot.returnEmailFromToken(token);
        forgetPasswordTokenRepo.deleteByEmail(email);
        forgetPasswordTokenRepo.flush();
    }


}
