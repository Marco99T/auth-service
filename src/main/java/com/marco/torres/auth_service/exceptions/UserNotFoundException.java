package com.marco.torres.auth_service.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Usuario no encontrado");
    }
}
