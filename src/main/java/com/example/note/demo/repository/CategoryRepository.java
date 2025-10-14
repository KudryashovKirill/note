package com.example.note.demo.repository;

import com.example.note.demo.model.Category;
import com.example.note.demo.util.exception.NoDataFoundException;
import com.example.note.demo.util.exception.ObjectAlreadyInTableException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryRepository {
    JdbcTemplate template;
    SimpleJdbcInsert insert;

    @Autowired
    public CategoryRepository(JdbcTemplate template) {
        this.template = template;
        this.insert = new SimpleJdbcInsert(template)
                .withTableName("categories")
                .usingGeneratedKeyColumns("id");
    }

    @Transactional
    public Category save(Category category) {
        if (findCategoryByName(category.getName()) != null) {
            throw new ObjectAlreadyInTableException(String.format("Category with name %s already in table",
                    category.getName()));
        }
        Map<String, Object> values = new HashMap<>();
        values.put("name", category.getName());

        Number id = insert.executeAndReturnKey(values);
        category.setId(id.longValue());
        return category;
    }

    public Category getById(Long id) {
        String sqlQuery = """
                SELECT * 
                FROM categories 
                WHERE id = ?
                """;
        try {
            return template.queryForObject(sqlQuery, (rs, rowNum) -> {
                Category category = new Category();
                category.setId(rs.getLong("id"));
                category.setName(rs.getString("name"));
                return category;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoDataFoundException("no category found by id");
        }
    }

    @Transactional
    public Category update(Category category, Long id) {
        String sqlQuery = """
                UPDATE categories
                SET name = ?
                WHERE id = ?
                """;
        template.update(sqlQuery, category.getName(), id);
        return category;
    }

    @Transactional
    public Map<String, Boolean> delete(Long id) {
        String sqlQuery = """
                DELETE FROM categories
                WHERE id = ?
                """;
        int countOfUpdate = template.update(sqlQuery, id);
        return Map.of("deleted", countOfUpdate > 0);
    }

    private Long findCategoryByName(String name) {
        String sqlQuery = """
                SELECT id
                FROM categories
                WHERE name = ?
                """;
        try {
            return template.queryForObject(sqlQuery, Long.class, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
