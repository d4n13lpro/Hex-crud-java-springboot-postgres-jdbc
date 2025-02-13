package com.daniel.crud_hexa_docker_env.infraestructure.adapter.controller;

import com.daniel.crud_hexa_docker_env.application.service.BookService;
import com.daniel.crud_hexa_docker_env.domain.model.Book;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createBook(@RequestBody Object requestBody) {
        if (requestBody instanceof List<?>) {
            List<?> rawBooks = (List<?>) requestBody;
            List<Book> books = rawBooks.stream()
                    .filter(item -> item instanceof Map)
                    .map(item -> {
                        Map<?, ?> bookMap = (Map<?, ?>) item;
                        return new Book(null, (String) bookMap.get("name"));
                    })
                    .toList();

            Map<String, Object> result = bookService.createBooks(books);
            return ResponseEntity.ok(result);
        } else if (requestBody instanceof Map<?, ?> bookMap) {
            if (bookMap.containsKey("name")) {
                Book book = new Book(null, (String) bookMap.get("name"));
                Book createdBook = bookService.createBook(book);
                return ResponseEntity.ok(Map.of("id", createdBook.getId()));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid request format"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Map<String, String> bookMap) {
        if (!bookMap.containsKey("name")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing 'name' field"));
        }
        Book book = new Book(id, bookMap.get("name"));
        Book updatedBook = bookService.updateBook(id, book);
        return ResponseEntity.ok(Map.of("id", updatedBook.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Long>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(Map.of("id", id));
    }
}
