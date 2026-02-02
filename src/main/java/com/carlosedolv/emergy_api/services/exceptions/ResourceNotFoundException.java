package com.carlosedolv.emergy_api.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Object reference) {
        super("Resource not found: " + reference);
    }
}
