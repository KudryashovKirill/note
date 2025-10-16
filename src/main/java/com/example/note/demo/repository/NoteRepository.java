package com.example.note.demo.repository;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.*;
import com.example.note.demo.util.exception.NoDataFoundException;
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
import java.time.LocalDate;
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
    public Note save(Note note, List<CategoryDto> categoryNames, List<TagDto> tags) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", note.getName());
        values.put("date_of_creation", note.getDateOfCreation());
        values.put("date_of_update", note.getDateOfUpdate());
        values.put("is_done", note.getIsDone());

        Number id = insert.executeAndReturnKey(values);
        note.setId(id.longValue());

        List<Long> categoryIds = new ArrayList<>();
        for (CategoryDto categoryDto : categoryNames) {
            Long categoryId = findCategoryByName(categoryDto.getName());
            if (categoryId == null) {
                categoryId = insertCategory(categoryDto.getName());
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
        return findByIdWithRelations(note.getId());
    }

    public Note getById(Long id) {
        String sqlQuery = """
                SELECT * 
                FROM notes
                WHERE id = ?
                """;
        try {
            return template.queryForObject(sqlQuery, (rs, rowNum) -> mapNote(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoDataFoundException("No note found by id");
        }
    }

    public List<Note> getAll() {
        String sqlQuery = """
                SELECT *
                FROM notes
                """;
        return template.query(sqlQuery, (rs, rowNum) -> mapNote(rs));
    }

    @Transactional
    public Note update(Note note, Long id) {
        String sqlQuery = """
                UPDATE notes
                SET name = ?, date_of_creation = ?, date_of_update = ?
                WHERE id = ?
                """;
        template.update(sqlQuery, note.getName(), note.getDateOfCreation(), note.getDateOfUpdate(), id);
        return findByIdWithRelations(id);
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
                .withTableName("tags")
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

    private Note findByIdWithRelations(Long noteId) {
        String noteSql = "SELECT * FROM notes WHERE id = ?";
        Note note = template.queryForObject(noteSql, (rs, rowNum) -> mapNote(rs), noteId);

        String categoriesSql = """
                SELECT c.* FROM categories c
                JOIN note_category nc ON c.id = nc.category_id
                WHERE nc.note_id = ?
                """;
        List<NoteCategory> noteCategories = template.query(categoriesSql, (rs, rowNum) -> {
            Category category = new Category();
            category.setId(rs.getLong("id"));
            category.setName(rs.getString("name"));

            NoteCategory noteCategory = new NoteCategory();
            noteCategory.setCategory(category);
            noteCategory.setNote(note);
            return noteCategory;
        }, noteId);
        note.setNoteCategories(noteCategories);

        String tagsSql = """
                SELECT t.* FROM tags t
                JOIN note_tag nt ON t.id = nt.tag_id
                WHERE nt.note_id = ?
                """;
        List<NoteTag> noteTags = template.query(tagsSql, (rs, rowNum) -> {
            Tag tag = new Tag();
            tag.setId(rs.getLong("id"));
            tag.setName(rs.getString("name"));
            tag.setColour(rs.getString("colour"));

            NoteTag noteTag = new NoteTag();
            noteTag.setTag(tag);
            noteTag.setNote(note);
            return noteTag;
        }, noteId);
        note.setNoteTags(noteTags);

        return note;
    }

    private Note mapNote(ResultSet rs) throws SQLException {
        Note n = new Note();
        n.setId(rs.getLong("id"));
        n.setName(rs.getString("name"));
        n.setDateOfCreation(rs.getObject("date_of_creation", LocalDate.class));
        n.setDateOfUpdate(rs.getObject("date_of_update", LocalDate.class));
        n.setIsDone(rs.getBoolean("is_done"));
        return n;
    }
}
