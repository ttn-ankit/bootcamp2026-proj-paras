package org.example.ecommerce.GlobalExceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Object> handleDuplicateEmailException(DuplicateEmailException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(InvalidEmail.class)
    public ResponseEntity<Object> handleInvalidEmailException(InvalidEmail ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
        @ExceptionHandler(InvalidJwtToken.class)
    public ResponseEntity<Object> handleInvalidJwtTokenException(InvalidJwtToken ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        @ExceptionHandler(AccountNotActiveException.class)
    public ResponseEntity<Object> handleAccountNotActiveException(AccountNotActiveException ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
        @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
        @ExceptionHandler(PasswordDoesNotMatchException.class)
    public ResponseEntity<Object> handlePasswordDoesNotMatchException(PasswordDoesNotMatchException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        @ExceptionHandler(NotPermitted.class)
    public ResponseEntity<Object> handleNotPermittedException(NotPermitted ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
}
