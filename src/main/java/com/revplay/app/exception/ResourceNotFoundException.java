package com.revplay.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RevPlayException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
