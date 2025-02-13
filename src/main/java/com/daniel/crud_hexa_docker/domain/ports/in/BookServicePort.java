package com.daniel.crud_hexa_docker.domain.ports.in;

import com.daniel.crud_hexa_docker.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookServicePort {
    Book createBook(Book book);
    Optional<Book> getBookById(Long id);
    List<Book> getAllBooks();
    Book updateBook(Long id, Book book);
    void deleteBook(Long id);
}
