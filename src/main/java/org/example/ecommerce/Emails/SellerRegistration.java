package org.example.ecommerce.Emails;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SellerRegistration {
     JavaMailSender mailSender;

    @Async
    public void sendRegistrationStatusEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Registration Details Submitted for Seller Account");
        message.setText("We have Successfully Received your Application for Registration as a Seller " +
                "to our website Ecommerce " +
                "We will get back to you once we verify your details");
        mailSender.send(message);
    }
}
