package com.ironlibrary.book_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironlibrary.book_service.model.Book;
import com.ironlibrary.book_service.model.Category;
import com.ironlibrary.book_service.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para BookController usando MockMvc
 * Compatible con Spring Boot 3.4+ (sin @MockBean deprecated)
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class BookControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService; // Este será el mock

    @Autowired
    private ObjectMapper objectMapper;

    private Book testBook;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public BookService bookService() {
            return mock(BookService.class);
        }
    }

    @BeforeEach
    void setUp() {
        // Reset mock before each test
        reset(bookService);

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
    void getAllBooks_ShouldReturnBookListAsJson() throws Exception {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookService.findAllBooks()).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Cien años de soledad"))
                .andExpect(jsonPath("$[0].author").value("Gabriel García Márquez"))
                .andExpect(jsonPath("$[0].category").value("FICTION"))
                .andExpect(jsonPath("$[0].availableCopies").value(3));
    }

    @Test
    void getBookById_ShouldReturnBookAsJson() throws Exception {
        // Given
        when(bookService.findBookById(1L)).thenReturn(testBook);

        // When & Then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Cien años de soledad"));
    }

    @Test
    void getAvailableBooks_ShouldReturnOnlyAvailableBooks() throws Exception {
        // Given
        List<Book> availableBooks = Arrays.asList(testBook);
        when(bookService.findAvailableBooks()).thenReturn(availableBooks);

        // When & Then
        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].availableCopies").value(3));
    }

    @Test
    void isBookAvailable_ShouldReturnBooleanForAvailability() throws Exception {
        // Given
        when(bookService.isBookAvailable(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/books/1/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    void createBook_ShouldReturnCreatedBookWithStatus201() throws Exception {
        // Given
        Book newBook = new Book();
        newBook.setTitle("El Principito");
        newBook.setAuthor("Antoine de Saint-Exupéry");
        newBook.setIsbn("978-84-376-0123-4");
        newBook.setCategory(Category.FICTION);
        newBook.setTotalCopies(3);
        newBook.setAvailableCopies(3);

        when(bookService.saveBook(any(Book.class))).thenReturn(testBook);

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Cien años de soledad"));
    }

    @Test
    void updateAvailability_ShouldReturnOk() throws Exception {
        // Given
        doNothing().when(bookService).updateAvailability(1L, -1);

        // When & Then
        mockMvc.perform(patch("/api/books/1/availability")
                        .param("copies", "-1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBook_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(bookService).deleteBook(1L);

        // When & Then
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void healthCheck_ShouldReturnHealthMessage() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/books/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book Service is running on port 8081"));
    }
}
