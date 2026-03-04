package org.example.ecommerce.GlobalExceptions;

public class AccountNotActiveException extends RuntimeException{
    public AccountNotActiveException(String msg){
        super(msg);
    }
}
