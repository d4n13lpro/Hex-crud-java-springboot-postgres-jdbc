package com.daniel.crud_hexa_docker_env.infraestructure.adapter.driven.repository;

import com.daniel.crud_hexa_docker_env.domain.model.Book;
import com.daniel.crud_hexa_docker_env.domain.ports.out.BookRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository implements BookRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> new Book(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("author"),
            rs.getInt("publication_year") // 🛠️ Corregido: antes decía publicationYear
    );

    @Override
    public Book save(Book book) {
        String sql = "INSERT INTO books (name, author, publication_year) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, book.getName(), book.getAuthor(), book.getPublicationYear());

        // Recuperar el ID generado
        String queryLastId = "SELECT id FROM books WHERE name = ? ORDER BY id DESC LIMIT 1";
        Long id = jdbcTemplate.queryForObject(queryLastId, Long.class, book.getName());

        book.setId(id);
        return book;
    }

    @Override
    public List<Book> saveAll(List<Book> books) {
        for (Book book : books) {
            save(book);
        }
        return books;
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper, id);
        return books.stream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM books";
        return jdbcTemplate.query(sql, bookRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM books WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }
}
