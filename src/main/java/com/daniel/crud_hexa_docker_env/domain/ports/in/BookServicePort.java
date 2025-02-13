package com.daniel.crud_hexa_docker_env.domain.ports.in;

import com.daniel.crud_hexa_docker_env.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookServicePort {
    Book createBook(Book book);
    List<Book> createBooks(List<Book> books);
    Optional<Book> getBookById(Long id);
    List<Book> getAllBooks();
    Book updateBook(Long id, Book book);
    void deleteBook(Long id);

    boolean bookExists(String name); // Nuevo método para validar duplicados
}
