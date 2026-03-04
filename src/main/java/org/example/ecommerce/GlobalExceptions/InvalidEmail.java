package org.example.ecommerce.GlobalExceptions;

public class InvalidEmail extends RuntimeException{
    public InvalidEmail(String msg){
        super(msg);
    }
}
