package com.ironlibrary.book_service.service;

import com.ironlibrary.book_service.exception.BookNotFoundException;
import com.ironlibrary.book_service.exception.InsufficientCopiesException;
import com.ironlibrary.book_service.model.Book;
import com.ironlibrary.book_service.model.Category;
import com.ironlibrary.book_service.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para BookService
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

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
    void findAllBooks_ShouldReturnAllBooks() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);

        // When
        List<Book> result = bookService.findAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook.getTitle(), result.get(0).getTitle());
        verify(bookRepository).findAll();
    }

    @Test
    void findBookById_ShouldReturnBook_WhenBookExists() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        Book result = bookService.findBookById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void findBookById_ShouldThrowException_WhenBookNotExists() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        BookNotFoundException exception = assertThrows(
                BookNotFoundException.class,
                () -> bookService.findBookById(1L)
        );

        assertEquals("Libro no encontrado con ID: 1", exception.getMessage());
        verify(bookRepository).findById(1L);
    }

    @Test
    void saveBook_ShouldReturnSavedBook_WhenValidBook() {
        // Given
        when(bookRepository.existsByIsbn(testBook.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = bookService.saveBook(testBook);

        // Then
        assertNotNull(result);
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookRepository).existsByIsbn(testBook.getIsbn());
        verify(bookRepository).save(testBook);
    }

    @Test
    void saveBook_ShouldThrowException_WhenISBNExists() {
        // Given
        when(bookRepository.existsByIsbn(testBook.getIsbn())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.saveBook(testBook)
        );

        assertTrue(exception.getMessage().contains("Ya existe un libro con el ISBN"));
        verify(bookRepository).existsByIsbn(testBook.getIsbn());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void saveBook_ShouldSetAvailableCopies_WhenNull() {
        // Given
        testBook.setAvailableCopies(null);
        when(bookRepository.existsByIsbn(testBook.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        bookService.saveBook(testBook);

        // Then
        assertEquals(testBook.getTotalCopies(), testBook.getAvailableCopies());
        verify(bookRepository).save(testBook);
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook_WhenValidData() {
        // Given
        Book updatedData = new Book();
        updatedData.setTitle("Nuevo título");
        updatedData.setAuthor("Nuevo autor");
        updatedData.setIsbn("978-84-376-0495-8");
        updatedData.setCategory(Category.SCIENCE);
        updatedData.setTotalCopies(10);
        updatedData.setAvailableCopies(8);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.existsByIsbn(updatedData.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = bookService.updateBook(1L, updatedData);

        // Then
        assertEquals(updatedData.getTitle(), testBook.getTitle());
        assertEquals(updatedData.getAuthor(), testBook.getAuthor());
        verify(bookRepository).save(testBook);
    }

    @Test
    void deleteBook_ShouldDeleteBook_WhenBookExists() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        bookService.deleteBook(1L);

        // Then
        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(testBook);
    }

    @Test
    void updateAvailability_ShouldUpdateCopies_WhenValidData() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        bookService.updateAvailability(1L, -1);

        // Then
        assertEquals(2, testBook.getAvailableCopies());
        verify(bookRepository).save(testBook);
    }

    @Test
    void updateAvailability_ShouldThrowException_WhenInsufficientCopies() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When & Then
        InsufficientCopiesException exception = assertThrows(
                InsufficientCopiesException.class,
                () -> bookService.updateAvailability(1L, -5)
        );

        assertTrue(exception.getMessage().contains("No hay suficientes copias disponibles"));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateAvailability_ShouldThrowException_WhenExceedsTotalCopies() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.updateAvailability(1L, 5)
        );

        assertTrue(exception.getMessage().contains("no pueden exceder el total"));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void findByCategory_ShouldReturnBooksOfCategory() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByCategory(Category.FICTION)).thenReturn(books);

        // When
        List<Book> result = bookService.findByCategory(Category.FICTION);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository).findByCategory(Category.FICTION);
    }

    @Test
    void findAvailableBooks_ShouldReturnAvailableBooks() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAvailableBooks()).thenReturn(books);

        // When
        List<Book> result = bookService.findAvailableBooks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository).findAvailableBooks();
    }

    @Test
    void isBookAvailable_ShouldReturnTrue_WhenBookHasAvailableCopies() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        boolean result = bookService.isBookAvailable(1L);

        // Then
        assertTrue(result);
        verify(bookRepository).findById(1L);
    }

    @Test
    void isBookAvailable_ShouldReturnFalse_WhenBookHasNoAvailableCopies() {
        // Given
        testBook.setAvailableCopies(0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        boolean result = bookService.isBookAvailable(1L);

        // Then
        assertFalse(result);
        verify(bookRepository).findById(1L);
    }
}
