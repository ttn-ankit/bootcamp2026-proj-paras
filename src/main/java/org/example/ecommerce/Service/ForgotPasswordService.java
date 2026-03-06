package org.example.ecommerce.Service;

import jakarta.transaction.Transactional;
import org.example.ecommerce.Entity.ForgetPasswordToken;
import org.example.ecommerce.Entity.User;
import org.example.ecommerce.GlobalExceptions.AccountNotActiveException;
import org.example.ecommerce.GlobalExceptions.InvalidJwtToken;
import org.example.ecommerce.GlobalExceptions.NotPermitted;
import org.example.ecommerce.GlobalExceptions.UserNotFoundException;
import org.example.ecommerce.Repository.ForgetPasswordRepository;
import org.example.ecommerce.Repository.UserRepository;
import org.example.ecommerce.Tokens.JwtForgot;
import org.example.ecommerce.Tokens.JwtLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ForgotPasswordService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ForgetPasswordRepository forgetPasswordTokenRepo;

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
