package org.example.ecommerce.Emails;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRegistration {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(String toEmail, String token){
        String verificationUrl = "http://localhost:8080/api/user/verify/?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verify Your Email");
        message.setText("Click the link to verify your account: " + verificationUrl);
        mailSender.send(message);

    }
}
