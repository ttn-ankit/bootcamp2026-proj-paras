package org.example.ecommerce.GlobalExceptions;

public class NotLeafCategory extends RuntimeException {
    public NotLeafCategory(String message) {
        super(message);
    }
}
