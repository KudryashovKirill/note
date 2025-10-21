package com.example.note.demo.repository;

import com.example.note.demo.model.Tag;
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
public class TagRepository {
    JdbcTemplate template;
    SimpleJdbcInsert insert;

    @Autowired
    public TagRepository(JdbcTemplate template) {
        this.template = template;
        this.insert = new SimpleJdbcInsert(template)
                .withTableName("tags")
                .usingGeneratedKeyColumns("id");
    }

    @Transactional
    public Tag save(Tag tag) {
        log.info("Attempting to save tag with name: {} and colour: {}", tag.getName(), tag.getColour());
        if (findTagByName(tag.getName()) != null) {
            log.error("Tag with name '{}' already exists in the table", tag.getName());
            throw new ObjectAlreadyInTableException(String.format("Category with name %s already in table",
                    tag.getName()));
        }
        Map<String, Object> values = new HashMap<>();
        values.put("name", tag.getName());
        values.put("colour", tag.getColour());

        Number id = insert.executeAndReturnKey(values);
        tag.setId(id.longValue());
        log.info("Successfully saved tag with id: {}, name: {}, colour: {}",
                tag.getId(), tag.getName(), tag.getColour());
        return tag;
    }

    public Tag getById(Long id) {
        log.info("Getting tag by id: {}", id);
        String sqlQuery = """
                SELECT * 
                FROM tags
                WHERE id = ?
                """;
        try {
            return template.queryForObject(sqlQuery, (rs, rowNum) -> mapTag(rs), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("No tag found with id: {}", id);
            throw new NoDataFoundException("No tag found by id");
        }
    }

    public List<Tag> getAll() {
        log.info("Getting all tags");
        String sqlQuery = """
                SELECT *
                FROM tags
                """;
        log.info("Found tags");
        return template.query(sqlQuery, (rs, rowNum) -> mapTag(rs));
    }

    @Transactional
    public Tag update(Tag tag, Long id) {
        log.info("Updating tag with id: {} to name: {}, colour: {}", id, tag.getName(), tag.getColour());
        String sqlQuery = """
                UPDATE tags
                SET name = ?, colour = ?
                WHERE id = ?
                """;
        int updatedRows = template.update(sqlQuery, tag.getName(), tag.getColour(), id);
        tag.setId(id);
        log.info("Updated {} rows for tag id: {}", updatedRows, id);
        return tag;
    }

    @Transactional
    public Map<String, Boolean> delete(Long id) {
        log.info("Deleting tag with id: {}", id);
        String sqlQuery = """
                DELETE FROM tags
                WHERE id = ?
                """;
        int countOfUpdate = template.update(sqlQuery, id);
        log.info("Tag deletion result for id {}: {}", id, countOfUpdate > 0);
        return Map.of("deleted", countOfUpdate > 0);
    }

    private Long findTagByName(String name) {
        log.info("Searching for tag by name: {}", name);
        String sqlQuery = """
                SELECT id
                FROM tags
                WHERE name = ?
                """;
        try {
            return template.queryForObject(sqlQuery, Long.class, name);
        } catch (EmptyResultDataAccessException e) {
            log.info("No tag found with name: {}", name);
            return null;
        }
    }

    private Tag mapTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("name"));
        tag.setColour(rs.getString("colour"));
        return tag;
    }
}
