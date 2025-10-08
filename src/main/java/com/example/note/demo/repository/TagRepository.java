package com.example.note.demo.repository;

import com.example.note.demo.model.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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
        return template.queryForObject(sqlQuery, (rs, rowNum) -> {
            Tag tag = new Tag();
            tag.setName(rs.getString("name"));
            tag.setColour(rs.getString("colour"));
            return tag;
        }, id);
    }

    @Transactional
    public Tag update(Tag tag, Long id) {
        String sqlQuery = """
                UPDATE tags
                SET name = ?, colour = ?
                WHERE id = ?
                """;
        template.update(sqlQuery, tag.getName(), tag.getColour(), id);
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
}
