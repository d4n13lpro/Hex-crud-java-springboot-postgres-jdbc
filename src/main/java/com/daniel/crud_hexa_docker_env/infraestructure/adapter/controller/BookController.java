package com.daniel.crud_hexa_docker_env.infraestructure.adapter.controller;

import com.daniel.crud_hexa_docker_env.application.service.BookService;
import com.daniel.crud_hexa_docker_env.domain.model.Book;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public Map<String, Long> createBook(@RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return Map.of("id", createdBook.getId());
    }

    @GetMapping("/{id}")
    public Optional<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PutMapping("/{id}")
    public Map<String, Long> updateBook(@PathVariable Long id, @RequestBody Book book) {
        Book updatedBook = bookService.updateBook(id, book);
        return Map.of("id", updatedBook.getId());
    }

    @DeleteMapping("/{id}")
    public Map<String, Long> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return Map.of("id", id);
    }
}
