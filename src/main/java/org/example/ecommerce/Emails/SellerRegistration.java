package org.example.ecommerce.Emails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SellerRegistration {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendRegistrationStatusEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("anurag2@tothenew.com");
        message.setSubject("Registration Details Submitted for Seller Account");
        message.setText("We have Successfully Received your Application for Registration as a Seller " +
                "to our website Ecommerce " +
                "We will get back to you once we verify your details");
        mailSender.send(message);
    }
}
