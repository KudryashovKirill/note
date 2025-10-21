package com.example.note.demo.repository;

import com.example.note.demo.model.Tag;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (findTagByName(tag.getName()) != null) {
            throw new ObjectAlreadyInTableException(String.format("Category with name %s already in table",
                    tag.getName()));
        }
        Map<String, Object> values = new HashMap<>();
        values.put("name", tag.getName());
        values.put("colour", tag.getColour());

        Number id = insert.executeAndReturnKey(values);
        tag.setId(id.longValue());
        return tag;
    }

    public Tag getById(Long id) {
        String sqlQuery = """
                SELECT * 
                FROM tags
                WHERE id = ?
                """;
        try {
            return template.queryForObject(sqlQuery, (rs, rowNum) -> mapTag(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoDataFoundException("No tag found by id");
        }
    }

    public List<Tag> getAll() {
        String sqlQuery = """
                SELECT *
                FROM tags
                """;
        return template.query(sqlQuery, (rs, rowNum) -> mapTag(rs));
    }

    @Transactional
    public Tag update(Tag tag, Long id) {
        String sqlQuery = """
                UPDATE tags
                SET name = ?, colour = ?
                WHERE id = ?
                """;
        template.update(sqlQuery, tag.getName(), tag.getColour(), id);
        tag.setId(id);
        return tag;
    }

    @Transactional
    public Map<String, Boolean> delete(Long id) {
        String sqlQuery = """
                DELETE FROM tags
                WHERE id = ?
                """;
        int countOfUpdate = template.update(sqlQuery, id);
        return Map.of("deleted", countOfUpdate > 0);
    }

    private Long findTagByName(String name) {
        String sqlQuery = """
                SELECT id
                FROM tags
                WHERE name = ?
                """;
        try {
            return template.queryForObject(sqlQuery, Long.class, name);
        } catch (EmptyResultDataAccessException e) {
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
