package com.deustocoches.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.deustocoches.model.Book;
import com.deustocoches.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("deprecation") // Suprimir advertencia de MockBean
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Spring Boot in Action");
        book.setAuthor("Craig Walls");
    }

    @Test
    void testGetAllBooks() throws Exception {
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Clean Code");
        book2.setAuthor("Robert C. Martin");

        when(bookService.getAllBooks())
                .thenReturn(Arrays.asList(book, book2));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Spring Boot in Action"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Clean Code"));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void testGetBookById() throws Exception {
        when(bookService.getBookById(1L))
                .thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Spring Boot in Action"))
                .andExpect(jsonPath("$.author").value("Craig Walls"));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void testGetBookNotFound() throws Exception {
        when(bookService.getBookById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(999L);
    }

    @Test
    void testCreateBook() throws Exception {
        when(bookService.createBook(any(Book.class)))
                .thenReturn(book);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Spring Boot in Action"));

        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    void testCreateBookError() throws Exception {
        when(bookService.createBook(any(Book.class)))
                .thenThrow(new RuntimeException("Error creating book"));

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest());

        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    void testUpdateBook() throws Exception {
        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Spring Boot in Action (2nd Edition)");
        updatedBook.setAuthor("Craig Walls");

        when(bookService.updateBook(eq(1L), any(Book.class)))
                .thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Spring Boot in Action (2nd Edition)"));

        verify(bookService, times(1)).updateBook(eq(1L), any(Book.class));
    }

    @Test
    void testUpdateBookNotFound() throws Exception {
        when(bookService.updateBook(eq(999L), any(Book.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/books/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).updateBook(eq(999L), any(Book.class));
    }

    @Test
    void testDeleteBook() throws Exception {
        when(bookService.getBookById(1L))
                .thenReturn(Optional.of(book));

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).getBookById(1L);
        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void testDeleteBookNotFound() throws Exception {
        when(bookService.getBookById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(999L);
        verify(bookService, times(0)).deleteBook(999L);
    }
}