package com.daniel.crud_hexa_docker.infraestructure.adapter.repository;

import com.daniel.crud_hexa_docker.domain.model.Book;
import com.daniel.crud_hexa_docker.domain.ports.out.BookRepositoryPort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository implements BookRepositoryPort {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertBook;
    private static final String TABLE_NAME = "books";

    public BookRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertBook = new SimpleJdbcInsert(dataSource)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("name", book.getName());
            Number newId = insertBook.executeAndReturnKey(params);
            book.setId(newId.longValue());
        } else {
            String sql = "UPDATE " + TABLE_NAME + " SET name = :name WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("name", book.getName())
                    .addValue("id", book.getId());
            jdbcTemplate.update(sql, params);
        }
        return book;
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
        List<Book> books = jdbcTemplate.query(sql, params, new BookRowMapper());
        return books.stream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return jdbcTemplate.query(sql, new BookRowMapper());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    private static class BookRowMapper implements org.springframework.jdbc.core.RowMapper<Book> {
        @Override
        public Book mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            return new Book(rs.getLong("id"), rs.getString("name"));
        }
    }
}
