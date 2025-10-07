package com.example.note.demo.repository;

import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.Note;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoteRepository {
    JdbcTemplate template;
    SimpleJdbcInsert insert;

    @Autowired
    public NoteRepository(JdbcTemplate template) {
        this.template = template;
        this.insert = new SimpleJdbcInsert(template)
                .withTableName("notes")
                .usingGeneratedKeyColumns("id");
    }

    @Transactional
    public Note save(Note note, List<String> categoryNames, List<TagDto> tags) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", note.getName());
        values.put("date_of_creation", note.getDateOfCreation());
        values.put("date_of_update", note.getDateOfUpdate());
        values.put("is_done", note.getIsDone());

        Number id = insert.executeAndReturnKey(values);
        note.setId(id.longValue());

        List<Long> categoryIds = new ArrayList<>();
        for (String name : categoryNames) {
            Long categoryId = findCategoryByName(name);
            if (categoryId == null) {
                categoryId = insertCategory(name);
            }
            categoryIds.add(categoryId);
        }
        batchInsertNoteCategory(note.getId(), categoryIds);

        List<Long> tagIds = new ArrayList<>();
        for (TagDto tag : tags) {
            Long tagId = findTagByName(tag.getName());
            if (tagId == null) {
                tagId = insertTag(tag.getName(), tag.getColour() == null ? "#000000" : tag.getColour());
            }
            tagIds.add(tagId);
        }
        batchInsertNoteTag(note.getId(), tagIds);
        return note;
    }

    public Note getById(Long id) {
        String sqlQuery = """
                SELECT * 
                FROM notes
                WHERE id = ?
                """;
        return template.queryForObject(sqlQuery, (rs, rowNum) -> {
            Note note = new Note();
            note.setName(rs.getString("name"));
            note.setId(rs.getLong("id"));
            note.setDateOfCreation(rs.getDate("date_of_creation").toLocalDate());
            note.setDateOfUpdate(rs.getDate("date_of_update").toLocalDate());
            return note;
        }, id);
    }

    @Transactional
    public Note update(Note note, Long id) {
        String sqlQuery = """
                UPDATE notes
                SET name = ?, date_of_creation = ?, date_of_update = ?
                WHERE id = ?
                """;
        template.update(sqlQuery, note.getName(), note.getDateOfCreation(), note.getDateOfUpdate(), id);
        return note;
    }

    @Transactional
    public Map<String, Boolean> delete(Long id) {
        String sqlQuery = """
                DELETE FROM notes
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

    private Long insertCategory(String name) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
                .withTableName("categories")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = Map.of("name", name);
        return insert.executeAndReturnKey(values).longValue();
    }

    private void batchInsertNoteCategory(Long noteId, List<Long> categoryIds) {
        String sqlQuery = """
                INSERT INTO note_category (note_id, category_id)
                VALUES (?, ?)
                """;
        List<Object[]> args = categoryIds.stream()
                .map(id -> new Object[]{noteId, id})
                .toList();
        template.batchUpdate(sqlQuery, args);
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

    private Long insertTag(String name, String colour) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
                .withTableName("tag")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = Map.of("name", name, "colour", colour);
        return insert.executeAndReturnKey(values).longValue();
    }

    private void batchInsertNoteTag(Long noteId, List<Long> tagIds) {
        String sqlQuery = """
                INSERT INTO note_tag (note_id, tag_id)
                VALUES (?, ?)
                """;
        List<Object[]> args = tagIds.stream()
                .map(id -> new Object[]{noteId, id})
                .toList();
        template.batchUpdate(sqlQuery, args);
    }
}
