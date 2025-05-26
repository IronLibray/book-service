package com.ironlibrary.book_service.controller;

import com.ironlibrary.book_service.model.Book;
import com.ironlibrary.book_service.model.Category;
import com.ironlibrary.book_service.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Cien años de soledad");
        testBook.setAuthor("Gabriel García Márquez");
        testBook.setIsbn("978-84-376-0495-7");
        testBook.setCategory(Category.FICTION);
        testBook.setTotalCopies(5);
        testBook.setAvailableCopies(3);
    }

    @Test
    void getAllBooks_ShouldReturnBookList() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookService.findAllBooks()).thenReturn(books);

        // When
        ResponseEntity<List<Book>> response = bookController.getAllBooks();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Cien años de soledad", response.getBody().get(0).getTitle());
        verify(bookService).findAllBooks();
    }

    @Test
    void getBookById_ShouldReturnBook() {
        // Given
        when(bookService.findBookById(1L)).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.getBookById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cien años de soledad", response.getBody().getTitle());
        assertEquals("Gabriel García Márquez", response.getBody().getAuthor());
        verify(bookService).findBookById(1L);
    }

    @Test
    void getAvailableBooks_ShouldReturnAvailableBooks() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookService.findAvailableBooks()).thenReturn(books);

        // When
        ResponseEntity<List<Book>> response = bookController.getAvailableBooks();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).getAvailableCopies() > 0);
        verify(bookService).findAvailableBooks();
    }

    @Test
    void getBooksByCategory_ShouldReturnBooksOfCategory() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookService.findByCategory(Category.FICTION)).thenReturn(books);

        // When
        ResponseEntity<List<Book>> response = bookController.getBooksByCategory(Category.FICTION);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(Category.FICTION, response.getBody().get(0).getCategory());
        verify(bookService).findByCategory(Category.FICTION);
    }

    @Test
    void getBooksByAuthor_ShouldReturnBooksByAuthor() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookService.findByAuthor("García")).thenReturn(books);

        // When
        ResponseEntity<List<Book>> response = bookController.getBooksByAuthor("García");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).getAuthor().contains("García"));
        verify(bookService).findByAuthor("García");
    }

    @Test
    void getBooksByTitle_ShouldReturnBooksByTitle() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookService.findByTitle("Cien")).thenReturn(books);

        // When
        ResponseEntity<List<Book>> response = bookController.getBooksByTitle("Cien");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).getTitle().contains("Cien"));
        verify(bookService).findByTitle("Cien");
    }

    @Test
    void isBookAvailable_ShouldReturnTrue_WhenBookIsAvailable() {
        // Given
        when(bookService.isBookAvailable(1L)).thenReturn(true);

        // When
        ResponseEntity<Boolean> response = bookController.isBookAvailable(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(bookService).isBookAvailable(1L);
    }

    @Test
    void isBookAvailable_ShouldReturnFalse_WhenBookIsNotAvailable() {
        // Given
        when(bookService.isBookAvailable(1L)).thenReturn(false);

        // When
        ResponseEntity<Boolean> response = bookController.isBookAvailable(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(bookService).isBookAvailable(1L);
    }

    @Test
    void createBook_ShouldReturnCreatedBook() {
        // Given
        when(bookService.saveBook(any(Book.class))).thenReturn(testBook);

        // When
        ResponseEntity<Book> response = bookController.createBook(testBook);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cien años de soledad", response.getBody().getTitle());
        verify(bookService).saveBook(any(Book.class));
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook() {
        // Given
        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Nuevo título");
        updatedBook.setAuthor("Nuevo autor");
        updatedBook.setIsbn("978-84-376-0495-8");
        updatedBook.setCategory(Category.SCIENCE);
        updatedBook.setTotalCopies(10);
        updatedBook.setAvailableCopies(8);

        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(updatedBook);

        // When
        ResponseEntity<Book> response = bookController.updateBook(1L, updatedBook);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nuevo título", response.getBody().getTitle());
        assertEquals("Nuevo autor", response.getBody().getAuthor());
        verify(bookService).updateBook(eq(1L), any(Book.class));
    }

    @Test
    void updateAvailability_ShouldReturnOk() {
        // Given
        doNothing().when(bookService).updateAvailability(1L, -1);

        // When
        ResponseEntity<Void> response = bookController.updateAvailability(1L, -1);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookService).updateAvailability(1L, -1);
    }

    @Test
    void deleteBook_ShouldReturnNoContent() {
        // Given
        doNothing().when(bookService).deleteBook(1L);

        // When
        ResponseEntity<Void> response = bookController.deleteBook(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService).deleteBook(1L);
    }

    @Test
    void healthCheck_ShouldReturnOk() {
        // When
        ResponseEntity<String> response = bookController.healthCheck();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book Service is running on port 8081", response.getBody());
    }
}
