package com.revplay.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RevPlayException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
