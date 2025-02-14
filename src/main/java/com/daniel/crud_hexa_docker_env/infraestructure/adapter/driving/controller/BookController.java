package com.daniel.crud_hexa_docker_env.infraestructure.adapter.driving.controller;

import com.daniel.crud_hexa_docker_env.application.service.BookService;
import com.daniel.crud_hexa_docker_env.domain.model.Book;
import com.daniel.crud_hexa_docker_env.domain.exception.NotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<?> createBooks(@RequestBody Object requestBody) {
        try {
            if (requestBody instanceof List<?>) {
                // Caso 1: Es una lista de libros
                List<Book> books = ((List<?>) requestBody).stream()
                        .filter(item -> item instanceof Map)
                        .map(item -> {
                            Map<?, ?> bookMap = (Map<?, ?>) item;
                            return new Book(
                                    null,
                                    (String) bookMap.get("name"),
                                    (String) bookMap.get("author"),
                                    (Integer) bookMap.get("publicationYear")
                            );
                        })
                        .toList();

                Map<String, Object> result = bookService.createBooks(books);
                return ResponseEntity.ok(result);
            } else if (requestBody instanceof Map<?, ?>) {
                // Caso 2: Es un solo libro
                Map<?, ?> bookMap = (Map<?, ?>) requestBody;
                Book book = new Book(
                        null,
                        (String) bookMap.get("name"),
                        (String) bookMap.get("author"),
                        (Integer) bookMap.get("publicationYear")
                );

                Book createdBook = bookService.createBook(book);
                return ResponseEntity.ok(Map.of("id", createdBook.getId()));
            } else {
                // Caso 3: Formato no válido
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid request format"));
            }
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid request data"));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("No se encontró el libro con ID: " + id));
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book book) {
        try {
            Book updatedBook = bookService.updateBook(id, book);
            return ResponseEntity.ok(Map.of("id", updatedBook.getId()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            Long deletedId = bookService.deleteBook(id);
            return ResponseEntity.ok(Map.of("id", deletedId)); // Respuesta con el ID eliminado
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

}
