package com.daniel.crud_hexa_docker.application.service;

import com.daniel.crud_hexa_docker.domain.model.Book;
import com.daniel.crud_hexa_docker.domain.ports.in.BookServicePort;
import com.daniel.crud_hexa_docker.domain.ports.out.BookRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
