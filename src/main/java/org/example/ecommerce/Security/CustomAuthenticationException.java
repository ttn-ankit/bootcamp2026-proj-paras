package org.example.ecommerce.Security;

import javax.naming.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String explanation) {
        super(explanation);
    }
}
