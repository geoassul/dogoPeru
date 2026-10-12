package com.example.dogo_project.exception;

import org.springframework.http.HttpStatus;

/**
 * Error de regla de negocio (datos inválidos, stock insuficiente, etc.).
 * GlobalExceptionHandler lo convierte en una respuesta con el status indicado y el mensaje.
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
