package cinemania.exception;

import jakarta.validation.ValidationException;

public class JakartaValidationException extends ValidationException {
    public JakartaValidationException(String message) {
        super(message);
    }
}
