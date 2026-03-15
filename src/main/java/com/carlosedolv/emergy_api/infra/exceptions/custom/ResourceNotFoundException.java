package com.carlosedolv.emergy_api.infra.exceptions.custom;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Object reference) {
        super("Resource not found: " + reference);
    }
}
