package com.daniel.crud_hexa_docker_env.application.service;

import com.daniel.crud_hexa_docker_env.domain.model.Book;
import com.daniel.crud_hexa_docker_env.domain.ports.in.BookServicePort;
import com.daniel.crud_hexa_docker_env.domain.ports.out.BookRepositoryPort;
import com.daniel.crud_hexa_docker_env.domain.exception.NotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService implements BookServicePort {

    private final BookRepositoryPort bookRepositoryPort;

    public BookService(BookRepositoryPort bookRepositoryPort) {
        this.bookRepositoryPort = bookRepositoryPort;
    }

    @Override
    @Transactional
    public Book createBook(Book book) {
        if (book == null || book.getName() == null || book.getName().isBlank()) {
            throw new IllegalArgumentException("Book name cannot be null or empty");
        }
        if (bookRepositoryPort.existsByName(book.getName())) {
            throw new DuplicateKeyException("El libro '" + book.getName() + "' ya existe.");
        }
        return bookRepositoryPort.save(book);
    }

    @Override
    @Transactional
    public Map<String, Object> createBooks(List<Book> books) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Book list cannot be empty");
        }

        List<Book> existingBooks = books.stream()
                .filter(book -> bookRepositoryPort.existsByName(book.getName()))
                .toList();

        List<Book> booksToSave = books.stream()
                .filter(book -> !bookRepositoryPort.existsByName(book.getName()) && book.getName() != null && !book.getName().isBlank())
                .toList();

        List<Book> savedBooks = booksToSave.stream()
                .map(bookRepositoryPort::save)
                .toList();

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
    @Transactional
    public Book updateBook(Long id, Book book) {
        if (book == null || book.getName() == null || book.getName().isBlank()) {
            throw new IllegalArgumentException("Book data cannot be empty");
        }
        return bookRepositoryPort.findById(id)
                .map(existingBook -> {
                    if (!existingBook.getName().equals(book.getName()) && bookRepositoryPort.existsByName(book.getName())) {
                        throw new DuplicateKeyException("El libro con nombre '" + book.getName() + "' ya existe.");
                    }
                    existingBook.setName(book.getName());
                    existingBook.setAuthor(book.getAuthor());
                    existingBook.setPublicationYear(book.getPublicationYear());
                    return bookRepositoryPort.save(existingBook);
                })
                .orElseThrow(() -> new NotFoundException("Libro con ID " + id + " no encontrado."));
    }

    @Override
    @Transactional
    public Long deleteBook(Long id) {
        Book book = bookRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Libro con ID " + id + " no encontrado."));

        bookRepositoryPort.deleteById(book.getId());

        return book.getId(); // Retornar el ID del libro eliminado
    }


    @Override
    public boolean bookExists(String name) {
        return bookRepositoryPort.existsByName(name);
    }
}
