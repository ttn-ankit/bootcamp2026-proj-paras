package org.example.ecommerce.Emails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ForgotPassword {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendForgetPasswordEmail(String toEmail, String token){
        String verificationUrl = "http://localhost:8080/api/user/public/reset/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset Password ");
        message.setText("Click the link to reset the account password : " + verificationUrl);
        mailSender.send(message);

    }
}
