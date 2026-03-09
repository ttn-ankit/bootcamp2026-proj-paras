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
public class AccountActivated {
    JavaMailSender javaMailSender;

    @Async
    public void sendAccountActivatedEmail(String activated, String toEmail){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Account Activated");
        message.setText(activated);
        javaMailSender.send(message);
    }

    @Async
    public void sendAccountPasswordChangedEmail(String msg, String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Account Password Changed");
        message.setText(msg);
        javaMailSender.send(message);
    }

}
