package com.daniel.crud_hexa_docker_env.application.service;

import com.daniel.crud_hexa_docker_env.domain.model.Book;
import com.daniel.crud_hexa_docker_env.domain.ports.in.BookServicePort;
import com.daniel.crud_hexa_docker_env.domain.ports.out.BookRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService implements BookServicePort {

    private final BookRepositoryPort bookRepositoryPort;

    public BookService(BookRepositoryPort bookRepositoryPort) {
        this.bookRepositoryPort = bookRepositoryPort;
    }

    @Override
    public Book createBook(Book book) {
        return bookRepositoryPort.save(book);
    }

    public List<Book> createBooks(List<Book> books) {
        return books.stream()
                .map(bookRepositoryPort::save)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepositoryPort.findById(id);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepositoryPort.findAll();
    }

    @Override
    public Book updateBook(Long id, Book book) {
        return bookRepositoryPort.findById(id)
                .map(existingBook -> {
                    existingBook.setName(book.getName());
                    return bookRepositoryPort.save(existingBook);
                })
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    @Override
    public void deleteBook(Long id) {
        bookRepositoryPort.deleteById(id);
    }
}
