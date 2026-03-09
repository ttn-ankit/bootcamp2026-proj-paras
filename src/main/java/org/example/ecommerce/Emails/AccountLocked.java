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
public class AccountLocked {
    JavaMailSender mailSender;

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
