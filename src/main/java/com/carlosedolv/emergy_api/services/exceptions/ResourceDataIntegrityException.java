package com.carlosedolv.emergy_api.services.exceptions;

public class ResourceDataIntegrityException extends RuntimeException {
    public ResourceDataIntegrityException(String message) {
        super("Data Integrity error: " + message);
    }
}
