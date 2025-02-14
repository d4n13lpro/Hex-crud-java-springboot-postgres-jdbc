package com.daniel.crud_hexa_docker_env.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Book {
    private Long id;
    private String name;
    private String author;
    private Integer publicationYear;

    public Book(Long id, String name, String author, Integer publicationYear) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getAuthor () {
        return author;
    }

    public void setAuthor (String author) {
        this.author = author;
    }

    public Integer getPublicationYear () {
        return publicationYear;
    }

    public void setPublicationYear (Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return name == null || name.trim().isEmpty() ||
                author == null || author.trim().isEmpty() ||
                publicationYear == null;
    }

}
