package org.example.ecommerce.GlobalExceptions;

public class PasswordDoesNotMatchException extends RuntimeException{
    public PasswordDoesNotMatchException(String msg){
        super(msg);
    }
}
