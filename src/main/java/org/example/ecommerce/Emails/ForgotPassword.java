package org.example.ecommerce.Emails;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ForgotPassword {
    JavaMailSender mailSender;

    @Async
    public void sendForgetPasswordEmail(String toEmail, String token){
        String verificationUrl = "http://localhost:8080/api/user/reset?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset Password ");
        message.setText("Click the link to reset the account password : " + verificationUrl);
        mailSender.send(message);

    }
}
