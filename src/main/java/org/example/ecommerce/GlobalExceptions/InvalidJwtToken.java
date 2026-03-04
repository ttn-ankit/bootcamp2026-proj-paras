package org.example.ecommerce.GlobalExceptions;

public class InvalidJwtToken extends RuntimeException{
    public InvalidJwtToken(String msg)
    {
        super(msg);
    }

}
