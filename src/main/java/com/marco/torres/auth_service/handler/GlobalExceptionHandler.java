package com.marco.torres.auth_service.handler;

import java.util.Map;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.marco.torres.auth_service.exceptions.InvalidCredentialsException;
import com.marco.torres.auth_service.exceptions.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.SC_NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.SC_UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }
}
