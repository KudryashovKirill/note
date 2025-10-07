package com.example.note.demo.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Repository
public class NoteCategoryRepository {
    JdbcTemplate template;
}
