package com.ironlibrary.book_service.exception;

/**
 * Excepción lanzada cuando no hay suficientes copias disponibles
 */
public class InsufficientCopiesException extends RuntimeException {
    public InsufficientCopiesException(String message) {
        super(message);
    }
}
