package org.example.ecommerce.GlobalExceptions;


public class NotPermitted extends RuntimeException{
    public NotPermitted(String message) {
        super(message);
    }
}
