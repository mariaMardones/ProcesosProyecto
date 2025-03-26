package com.example.restapi.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.uDeusto.G15procesos.entity.Book;

@Repository
    public interface BookRepository extends JpaRepository<Book, Long> {
}