package com.carlosedolv.emergy_api.infra.exceptions.custom;

public class ResourceDataIntegrityException extends RuntimeException {
    public ResourceDataIntegrityException(String message) {
        super("Data Integrity error: " + message);
    }
}
