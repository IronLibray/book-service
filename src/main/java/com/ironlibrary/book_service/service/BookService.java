package com.ironlibrary.book_service.service;


import com.ironlibrary.book_service.exception.BookNotFoundException;
import com.ironlibrary.book_service.exception.InsufficientCopiesException;
import com.ironlibrary.book_service.model.Book;
import com.ironlibrary.book_service.model.Category;
import com.ironlibrary.book_service.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la lógica de negocio de libros
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Obtener todos los libros
     */
    @Transactional(readOnly = true)
    public List<Book> findAllBooks() {
        log.info("Obteniendo todos los libros");
        return bookRepository.findAll();
    }

    /**
     * Buscar libro por ID
     */
    @Transactional(readOnly = true)
    public Book findBookById(Long id) {
        log.info("Buscando libro con ID: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Libro no encontrado con ID: " + id));
    }

    /**
     * Guardar nuevo libro
     */
    public Book saveBook(Book book) {
        log.info("Guardando nuevo libro: {}", book.getTitle());

        // Verificar si el ISBN ya existe
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Ya existe un libro con el ISBN: " + book.getIsbn());
        }

        // Si no se especifican copias disponibles, usar el total
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }

        Book savedBook = bookRepository.save(book);
        log.info("Libro guardado exitosamente con ID: {}", savedBook.getId());
        return savedBook;
    }

    /**
     * Actualizar libro existente
     */
    public Book updateBook(Long id, Book bookUpdate) {
        log.info("Actualizando libro con ID: {}", id);
        Book existingBook = findBookById(id);

        // Verificar si el nuevo ISBN ya existe en otro libro
        if (!existingBook.getIsbn().equals(bookUpdate.getIsbn()) &&
                bookRepository.existsByIsbn(bookUpdate.getIsbn())) {
            throw new IllegalArgumentException("Ya existe un libro con el ISBN: " + bookUpdate.getIsbn());
        }

        existingBook.setTitle(bookUpdate.getTitle());
        existingBook.setAuthor(bookUpdate.getAuthor());
        existingBook.setIsbn(bookUpdate.getIsbn());
        existingBook.setCategory(bookUpdate.getCategory());
        existingBook.setTotalCopies(bookUpdate.getTotalCopies());
        existingBook.setAvailableCopies(bookUpdate.getAvailableCopies());

        Book updatedBook = bookRepository.save(existingBook);
        log.info("Libro actualizado exitosamente");
        return updatedBook;
    }

    /**
     * Eliminar libro
     */
    public void deleteBook(Long id) {
        log.info("Eliminando libro con ID: {}", id);
        Book book = findBookById(id);
        bookRepository.delete(book);
        log.info("Libro eliminado exitosamente");
    }

    /**
     * Actualizar disponibilidad de copias (para préstamos/devoluciones)
     */
    public void updateAvailability(Long id, int copies) {
        log.info("Actualizando disponibilidad del libro ID: {} con {} copias", id, copies);
        Book book = findBookById(id);
        int newAvailable = book.getAvailableCopies() + copies;

        if (newAvailable < 0) {
            throw new InsufficientCopiesException("No hay suficientes copias disponibles. Disponibles: "
                    + book.getAvailableCopies() + ", Solicitadas: " + Math.abs(copies));
        }

        if (newAvailable > book.getTotalCopies()) {
            throw new IllegalArgumentException("Las copias disponibles no pueden exceder el total de copias");
        }

        book.setAvailableCopies(newAvailable);
        bookRepository.save(book);
        log.info("Disponibilidad actualizada. Nuevas copias disponibles: {}", newAvailable);
    }

    /**
     * Buscar libros por categoría
     */
    @Transactional(readOnly = true)
    public List<Book> findByCategory(Category category) {
        log.info("Buscando libros por categoría: {}", category);
        return bookRepository.findByCategory(category);
    }

    /**
     * Obtener libros disponibles
     */
    @Transactional(readOnly = true)
    public List<Book> findAvailableBooks() {
        log.info("Obteniendo libros disponibles");
        return bookRepository.findAvailableBooks();
    }

    /**
     * Buscar libros por autor
     */
    @Transactional(readOnly = true)
    public List<Book> findByAuthor(String author) {
        log.info("Buscando libros por autor: {}", author);
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    /**
     * Buscar libros por título
     */
    @Transactional(readOnly = true)
    public List<Book> findByTitle(String title) {
        log.info("Buscando libros por título: {}", title);
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Verificar si un libro está disponible para préstamo
     */
    @Transactional(readOnly = true)
    public boolean isBookAvailable(Long id) {
        Book book = findBookById(id);
        return book.isAvailable();
    }
}
