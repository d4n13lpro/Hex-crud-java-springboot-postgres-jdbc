package com.daniel.crud_hexa_docker_env.application.service;

import com.daniel.crud_hexa_docker_env.domain.model.Book;
import com.daniel.crud_hexa_docker_env.domain.ports.in.BookServicePort;
import com.daniel.crud_hexa_docker_env.domain.ports.out.BookRepositoryPort;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (bookExists(book.getName())) {
            throw new DuplicateKeyException("El libro '" + book.getName() + "' ya existe.");
        }
        return bookRepositoryPort.save(book);
    }

    @Override
    public Map<String, Object> createBooks(List<Book> books) {
        List<Book> existingBooks = books.stream()
                .filter(book -> bookRepositoryPort.existsByName(book.getName()))
                .collect(Collectors.toList());

        List<Book> booksToSave = books.stream()
                .filter(book -> !bookRepositoryPort.existsByName(book.getName()))
                .collect(Collectors.toList());

        List<Book> savedBooks = booksToSave.stream()
                .map(bookRepositoryPort::save)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("savedBooks", savedBooks);
        response.put("existingBooks", existingBooks);

        return response;
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
                    if (!existingBook.getName().equals(book.getName()) && bookExists(book.getName())) {
                        throw new DuplicateKeyException("El libro con nombre '" + book.getName() + "' ya existe.");
                    }
                    existingBook.setName(book.getName());
                    return bookRepositoryPort.save(existingBook);
                })
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
    }

    @Override
    public void deleteBook(Long id) {
        bookRepositoryPort.deleteById(id);
    }

    @Override
    public boolean bookExists(String name) {
        return bookRepositoryPort.existsByName(name);
    }
}
