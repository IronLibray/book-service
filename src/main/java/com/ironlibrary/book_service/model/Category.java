package com.ironlibrary.book_service.model;

/**
 * Enum para las categorías de libros
 */
public enum Category {
    FICTION("Ficción"),
    NON_FICTION("No Ficción"),
    SCIENCE("Ciencia"),
    HISTORY("Historia");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}