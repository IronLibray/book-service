package com.ironlibrary.book_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false, length = 255)
    private String title;

    @NotBlank(message = "El autor es obligatorio")
    @Column(nullable = false, length = 255)
    private String author;

    @NotBlank(message = "El ISBN es obligatorio")
    @Column(unique = true, nullable = false, length = 20)
    private String isbn;

    // Cambio: usar @Enumerated con EnumType.STRING en lugar de usar columna enum nativa
    @Enumerated(EnumType.STRING)
    @NotNull(message = "La categoría es obligatoria")
    @Column(nullable = false, length = 50) // Agregamos longitud para compatibilidad
    private Category category;

    @Min(value = 1, message = "Debe haber al menos 1 copia")
    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;

    @Min(value = 0, message = "Las copias disponibles no pueden ser negativas")
    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    /**
     * Verifica si el libro está disponible para préstamo
     * @return true si hay copias disponibles
     */
    public boolean isAvailable() {
        return availableCopies != null && availableCopies > 0;
    }

    /**
     * Constructor para crear un libro con copias disponibles iguales al total
     */
    public Book(String title, String author, String isbn, Category category, Integer totalCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }
}
