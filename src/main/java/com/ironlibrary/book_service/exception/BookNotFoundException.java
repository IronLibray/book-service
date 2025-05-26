package com.ironlibrary.book_service.exception;

/**
 * Excepción lanzada cuando no se encuentra un libro
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
