package org.example.ecommerce.Emails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AccountLocked {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendAccountLockedEmail(String toEmail){

        String resetPasswordLink = "http://localhost:8080/api/user/forget-password";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Account Locked");
        message.setText("Your Account has been Locked. Please visit "+resetPasswordLink);
        mailSender.send(message);

    }
}
