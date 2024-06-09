package com.doanSpringboot.repository;

import com.doanSpringboot.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookRepository extends JpaRepository<Book, Long> {
}
