package com.ironlibrary.book_service.repository;


import com.ironlibrary.book_service.model.Book;
import com.ironlibrary.book_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Book
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Buscar libro por ISBN
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Buscar libros por categoría
     */
    List<Book> findByCategory(Category category);

    /**
     * Buscar libros por autor (búsqueda insensible a mayúsculas)
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);

    /**
     * Buscar libros por título (búsqueda insensible a mayúsculas)
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Obtener solo libros disponibles (con copias > 0)
     */
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAvailableBooks();

    /**
     * Buscar libros por autor y categoría
     */
    List<Book> findByAuthorContainingIgnoreCaseAndCategory(String author, Category category);

    /**
     * Contar libros por categoría
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.category = :category")
    Long countByCategory(@Param("category") Category category);

    /**
     * Verificar si existe un libro con el ISBN dado
     */
    boolean existsByIsbn(String isbn);
}
