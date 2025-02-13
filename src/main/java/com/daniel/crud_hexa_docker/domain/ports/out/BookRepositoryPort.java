package com.daniel.crud_hexa_docker.domain.ports.out;

import com.daniel.crud_hexa_docker.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepositoryPort {
    Book save(Book book);
    Optional<Book> findById(Long id);
    List<Book> findAll();
    void deleteById(Long id);
}
