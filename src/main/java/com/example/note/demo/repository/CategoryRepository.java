package com.example.note.demo.repository;

import com.example.note.demo.model.Category;
import com.example.note.demo.util.exception.NoDataFoundException;
import com.example.note.demo.util.exception.ObjectAlreadyInTableException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
        log.info("Attempting to save category with name: {}", category.getName());

        if (findCategoryByName(category.getName()) != null) {
            log.error("Category with name '{}' already exists in the table", category.getName());
            throw new ObjectAlreadyInTableException(String.format("Category with name %s already in table",
                    category.getName()));
        }
        Map<String, Object> values = new HashMap<>();
        values.put("name", category.getName());

        Number id = insert.executeAndReturnKey(values);
        category.setId(id.longValue());
        log.info("Successfully saved category with id: {} and name: {}", category.getId(), category.getName());
        return category;
    }

    public Category getById(Long id) {
        log.info("Getting category by id: {}", id);
        String sqlQuery = """
                SELECT * 
                FROM categories 
                WHERE id = ?
                """;
        try {
            log.info("Successfully found category with id: {}", id);
            return template.queryForObject(sqlQuery, (rs, rowNum) -> mapCategory(rs), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("No category found by id: {}", id);
            throw new NoDataFoundException("no category found by id");
        }
    }

    public List<Category> getAll() {
        log.info("Getting all categories");
        String sqlQuery = """
                SELECT * 
                FROM categories
                """;
        log.info("Found categories");
        return template.query(sqlQuery, (rs, rowNum) -> mapCategory(rs));
    }

    @Transactional
    public Category update(Category category, Long id) {
        log.info("Updating category with id: {} to name: {}", id, category.getName());
        String sqlQuery = """
                UPDATE categories
                SET name = ?
                WHERE id = ?
                """;
        template.update(sqlQuery, category.getName(), id);
        category.setId(id);
        log.info("Updated category id: {}", id);
        return category;
    }

    @Transactional
    public Map<String, Boolean> delete(Long id) {
        log.info("Deleting category with id: {}", id);
        String sqlQuery = """
                DELETE FROM categories
                WHERE id = ?
                """;
        int countOfUpdate = template.update(sqlQuery, id);
        log.info("Category delete result for id {}: {}", id, countOfUpdate > 0);
        return Map.of("deleted", countOfUpdate > 0);
    }

    private Long findCategoryByName(String name) {
        log.info("Searching for category by name: {}", name);
        String sqlQuery = """
                SELECT id
                FROM categories
                WHERE name = ?
                """;
        try {
            log.info("Found category with name '{}' and id", name);
            return template.queryForObject(sqlQuery, Long.class, name);
        } catch (EmptyResultDataAccessException e) {
            log.info("No category found with name: {}", name);
            return null;
        }
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        return category;
    }
}
