package com.daniel.crud_hexa_docker_env.infraestructure.adapter.controller;

import com.daniel.crud_hexa_docker_env.application.service.BookService;
import com.daniel.crud_hexa_docker_env.domain.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Object requestBody) {
        if (requestBody instanceof List<?>) { // Si recibe una lista de libros
            List<?> rawBooks = (List<?>) requestBody;
            List<Book> books = rawBooks.stream()
                    .filter(item -> item instanceof Map) // Filtra solo los mapas válidos
                    .map(item -> {
                        Map<?, ?> bookMap = (Map<?, ?>) item;
                        return new Book(null, (String) bookMap.get("name")); // null para id (autogenerado)
                    })
                    .collect(Collectors.toList());

            List<Book> createdBooks = bookService.createBooks(books);
            List<Map<String, Long>> response = createdBooks.stream()
                    .map(book -> Map.of("id", book.getId()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } else if (requestBody instanceof Map<?, ?>) { // Si recibe un solo libro
            Map<?, ?> bookMap = (Map<?, ?>) requestBody;
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
    public ResponseEntity<Map<String, Long>> updateBook(@PathVariable Long id, @RequestBody Map<String, String> bookMap) {
        if (!bookMap.containsKey("name")) {
            return ResponseEntity.badRequest().body(Map.of("error", -1L)); // -1 indica error
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
