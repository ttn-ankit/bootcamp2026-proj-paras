package org.example.ecommerce.GlobalExceptions;

import org.springframework.http.HttpStatus;

public class APIException extends RuntimeException {
    private final HttpStatus statusCode;
    public APIException(String msg, HttpStatus httpStatus){
        super(msg);
        this.statusCode = httpStatus;
    }
}
