package com.daniel.crud_hexa_docker_env.domain.ports.in;

import com.daniel.crud_hexa_docker_env.domain.model.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookServicePort {
    Book createBook(Book book);
    Map<String, Object> createBooks(List<Book> books); // Firma corregida para coincidir con la implementación
    Optional<Book> getBookById(Long id);
    List<Book> getAllBooks();
    Book updateBook(Long id, Book book);
    void deleteBook(Long id);
    boolean bookExists(String name);
}
