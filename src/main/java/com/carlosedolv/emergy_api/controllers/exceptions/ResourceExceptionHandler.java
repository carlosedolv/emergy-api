package com.carlosedolv.emergy_api.controllers.exceptions;

import com.carlosedolv.emergy_api.services.exceptions.ResourceDataIntegrityException;
import com.carlosedolv.emergy_api.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError error = new ValidationError(
                Instant.now(),
                status.value(),
                "Validation error",
                "Please check the fields below",
                request.getRequestURI()
        );

        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationError> validationConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422
        ValidationError err = new ValidationError(
                Instant.now(),
                status.value(),
                "Erro de validação no banco",
                "Dados inválidos",
                request.getRequestURI()
        );

        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            err.addError(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return ResponseEntity.status(status).body(err);
    }

    private ResponseEntity<StandardError> buildError(HttpStatus status, String error, String message, HttpServletRequest request) {
        StandardError standardError = new StandardError(
                Instant.now(), status.value(), error, message, request.getRequestURI()
        );
        return ResponseEntity.status(status).body(standardError);
    }
}
