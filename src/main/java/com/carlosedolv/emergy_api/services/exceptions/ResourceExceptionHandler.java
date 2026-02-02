package com.carlosedolv.emergy_api.services.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(ResourceDataIntegrityException.class)
    public ResponseEntity<StandardError> handleResourceDataIntegrity(ResourceDataIntegrityException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.CONFLICT,
                "Resource data integrity violation",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Error reading JSON",
                "Invalid data format. Check date fields or numeric types.",
                request

        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrity(DataIntegrityViolationException e, HttpServletRequest request) {
        return buildError(
                HttpStatus.CONFLICT,
                "Data integrity violation",
                "The operation violates a database constraint.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGenericException(Exception e, HttpServletRequest request) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "An unexpected error occurred on the server.",
                request
        );
    }

    private ResponseEntity<StandardError> buildError(HttpStatus status, String error, String message, HttpServletRequest request) {
        StandardError standardError = new StandardError(
                Instant.now(), status.value(), error, message, request.getRequestURI()
        );
        return ResponseEntity.status(status).body(standardError);
    }
}
