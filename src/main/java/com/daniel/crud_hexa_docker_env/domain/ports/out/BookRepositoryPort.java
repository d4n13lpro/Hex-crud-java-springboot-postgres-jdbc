package com.daniel.crud_hexa_docker_env.domain.ports.out;

import com.daniel.crud_hexa_docker_env.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepositoryPort {
    Book save(Book book);
    List<Book> saveAll(List<Book> books);
    Optional<Book> findById(Long id);
    List<Book> findAll();
    void deleteById(Long id);
    boolean existsByName(String name);
}
