package com.ironlibrary.book_service.controller;


import com.ironlibrary.book_service.model.Book;
import com.ironlibrary.book_service.model.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    /**
     * GET /api/books - Obtener todos los libros
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        log.info("Solicitud GET para obtener todos los libros");
        List<Book> books = bookService.findAllBooks();
        return ResponseEntity.ok(books);
    }

    /**
     * GET /api/books/{id} - Obtener libro por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        log.info("Solicitud GET para obtener libro con ID: {}", id);
        Book book = bookService.findBookById(id);
        return ResponseEntity.ok(book);
    }

    /**
     * GET /api/books/available - Obtener libros disponibles
     */
    @GetMapping("/available")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        log.info("Solicitud GET para obtener libros disponibles");
        List<Book> books = bookService.findAvailableBooks();
        return ResponseEntity.ok(books);
    }

    /**
     * GET /api/books/category?category=FICTION - Obtener libros por categoría
     */
    @GetMapping("/category")
    public ResponseEntity<List<Book>> getBooksByCategory(@RequestParam Category category) {
        log.info("Solicitud GET para obtener libros de categoría: {}", category);
        List<Book> books = bookService.findByCategory(category);
        return ResponseEntity.ok(books);
    }

    /**
     * GET /api/books/search/author?author=Garcia - Buscar por autor
     */
    @GetMapping("/search/author")
    public ResponseEntity<List<Book>> getBooksByAuthor(@RequestParam String author) {
        log.info("Solicitud GET para buscar libros por autor: {}", author);
        List<Book> books = bookService.findByAuthor(author);
        return ResponseEntity.ok(books);
    }

    /**
     * GET /api/books/search/title?title=Cien - Buscar por título
     */
    @GetMapping("/search/title")
    public ResponseEntity<List<Book>> getBooksByTitle(@RequestParam String title) {
        log.info("Solicitud GET para buscar libros por título: {}", title);
        List<Book> books = bookService.findByTitle(title);
        return ResponseEntity.ok(books);
    }

    /**
     * GET /api/books/{id}/available - Verificar disponibilidad
     */
    @GetMapping("/{id}/available")
    public ResponseEntity<Boolean> isBookAvailable(@PathVariable Long id) {
        log.info("Solicitud GET para verificar disponibilidad del libro ID: {}", id);
        boolean available = bookService.isBookAvailable(id);
        return ResponseEntity.ok(available);
    }

    /**
     * POST /api/books - Crear nuevo libro
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        log.info("Solicitud POST para crear nuevo libro: {}", book.getTitle());
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    /**
     * PUT /api/books/{id} - Actualizar libro completo
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book book) {
        log.info("Solicitud PUT para actualizar libro con ID: {}", id);
        Book updatedBook = bookService.updateBook(id, book);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * PATCH /api/books/{id}/availability?copies=-1 - Actualizar solo disponibilidad
     */
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> updateAvailability(@PathVariable Long id, @RequestParam int copies) {
        log.info("Solicitud PATCH para actualizar disponibilidad del libro ID: {} con {} copias", id, copies);
        bookService.updateAvailability(id, copies);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/books/{id} - Eliminar libro
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("Solicitud DELETE para eliminar libro con ID: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint de health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Book Service is running on port 8081");
    }
}
