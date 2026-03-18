package org.example.ecommerce.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.ResetPasswordDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.Emails.EmailService;
import org.example.ecommerce.Entity.ForgetPasswordToken;
import org.example.ecommerce.Entity.User;
import org.example.ecommerce.GlobalExceptions.*;
import org.example.ecommerce.Repository.ForgetPasswordRepository;
import org.example.ecommerce.Repository.UserRepository;
import org.example.ecommerce.Security.JWTService;
import org.springframework.http.HttpStatus;
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
     JWTService jwtService;

    public BasicResponse processForgotPassword(String email){

        validateEmail(email);

        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new APIException("Email does not exist", HttpStatus.BAD_REQUEST);
        }
        else{

            if(user.getIsLocked()){
                throw new APIException(
                        "Account is locked. You can not reset password. contact Admin",HttpStatus.BAD_REQUEST
                );
            }

            if(!user.getIsActive()){
                throw new APIException(
                        "Account is not active please contact admin or activate it by activation link",HttpStatus.BAD_REQUEST
                );
            }

            String token = jwtService.generatePasswordResetToken(email);

            setForgetTokenInDataBase(email, token);

            forgotPasswordEmail.sendEmail(token,email,"Reset Token");
        }
        return new BasicResponse("mail sent",200);
    }
    private void validateEmail(String email){

        if(email == null || email.trim().isEmpty()){
            throw new APIException("email is not valid",HttpStatus.BAD_REQUEST);
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if(!email.matches(emailRegex)){
            throw new APIException("email is not valid",HttpStatus.BAD_REQUEST);
        }
    }
    @Transactional
    public BasicResponse setPassword(String token, ResetPasswordDto resetPasswordDto) {
        if(!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirmPassword())){
            throw new APIException("Password does not match",HttpStatus.BAD_REQUEST);
        }
        String email = jwtService.extractUsername(token);
        if(!jwtService.validateToken(token,email)){
            throw new APIException("Token Expired", HttpStatus.UNAUTHORIZED);
        }
        ForgetPasswordToken forgetToken = forgetPasswordTokenRepo.findByEmail(email);
        if(!token.equals(forgetToken.getForgetToken())){
            throw new APIException("The Request Token is Changed. Please use latest One.", HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByEmail(email);
        if(user==null){
            throw new APIException("User Not Found with email: "+email, HttpStatus.BAD_REQUEST);
        }
        if(!user.getIsActive()){
            throw new APIException("Activate you account first",HttpStatus.BAD_REQUEST);
        }
        if(user.getIsLocked()){
            throw new APIException("Your Account is locked. Contact admin to unlock your account", HttpStatus.BAD_REQUEST);
        }
        String userPassword = passwordEncoder.encode(resetPasswordDto.getPassword());
        user.setPassword(userPassword);
        user.setInvalidAttemptCount(0);
        user.setPasswordUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        forgetPasswordTokenRepo.deleteById(email);
        return new BasicResponse("Password reset success", 200);
    }

    public void setForgetTokenInDataBase(String email, String token) {
        forgetPasswordTokenRepo.save(new ForgetPasswordToken(email,token));
    }
}
