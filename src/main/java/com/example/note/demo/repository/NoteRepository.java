package com.example.note.demo.repository;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.*;
import com.example.note.demo.util.exception.NoDataFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
        log.info("Saving new note with name: {}, categories: {}, tags: {}",
                note.getName(), categoryNames.size(), tags.size());
        Map<String, Object> values = new HashMap<>();
        values.put("name", note.getName());
        values.put("date_of_creation", note.getDateOfCreation());
        values.put("date_of_update", note.getDateOfUpdate());
        values.put("is_done", note.getIsDone());

        Number id = insert.executeAndReturnKey(values);
        note.setId(id.longValue());
        log.info("Note saved with id: {}", note.getId());

        List<Long> categoryIds = processCategories(categoryNames);
        batchInsertNoteCategory(note.getId(), categoryIds);
        log.info("Added {} categories to note", categoryIds.size());

        List<Long> tagIds = processTags(tags);
        batchInsertNoteTag(note.getId(), tagIds);
        log.info("Added {} tags to note", tagIds.size());

        return getById(note.getId());
    }

    public Note getById(Long id) {
        log.info("Fetching note with relations by id: {}", id);
        String sqlQuery = """
                SELECT 
                    n.id as note_id, n.name as note_name, n.date_of_creation, n.date_of_update, n.is_done,
                    c.id as category_id, c.name as category_name,
                    t.id as tag_id, t.name as tag_name, t.colour as tag_colour
                FROM notes n
                LEFT JOIN note_category nc ON n.id = nc.note_id
                LEFT JOIN categories c ON nc.category_id = c.id
                LEFT JOIN note_tag nt ON n.id = nt.note_id
                LEFT JOIN tags t ON nt.tag_id = t.id
                WHERE n.id = ?
                """;

        List<Note> notes = template.query(sqlQuery, new NoteWithRelationsExtractor(), id);
        if (notes.isEmpty()) {
            log.error("No note found with id: {}", id);
            throw new NoDataFoundException("No note found by id");
        }
        log.info("Found note: {} with {} categories and {} tags",
                notes.get(0).getName(), notes.get(0).getNoteCategories().size(), notes.get(0).getNoteTags().size());
        return notes.get(0);
    }


    public List<Note> getAll() {
        log.info("Getting all notes with relations");
        String sqlQuery = """
                SELECT 
                    n.id as note_id, n.name as note_name, n.date_of_creation, n.date_of_update, n.is_done,
                    c.id as category_id, c.name as category_name,
                    t.id as tag_id, t.name as tag_name, t.colour as tag_colour
                FROM notes n
                LEFT JOIN note_category nc ON n.id = nc.note_id
                LEFT JOIN categories c ON nc.category_id = c.id
                LEFT JOIN note_tag nt ON n.id = nt.note_id
                LEFT JOIN tags t ON nt.tag_id = t.id
                ORDER BY n.id
                """;
        log.info("Found notes with relations");
        return template.query(sqlQuery, new NoteWithRelationsExtractor());
    }

    @Transactional
    public Note update(Note note, Long id) {
        log.info("Updating note with id: {}, new name: {}", id, note.getName());
        String sqlQuery = """
                UPDATE notes
                SET name = ?, date_of_creation = ?, date_of_update = ?, is_done = ?
                WHERE id = ?
                """;
        int updatedRows = template.update(sqlQuery,
                note.getName(),
                note.getDateOfCreation(),
                note.getDateOfUpdate(),
                note.getIsDone(),
                id
        );
        log.info("Updated {} rows for note id: {}", updatedRows, id);
        return getById(id);
    }

    @Transactional
    public Map<String, Boolean> delete(Long id) {
        log.info("Deleting note with id: {}", id);
        String sqlQuery = """
                DELETE FROM notes
                WHERE id = ?
                """;
        int countOfUpdate = template.update(sqlQuery, id);
        log.info("Note deletion result for id {}: {}", id, countOfUpdate > 0);
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
            log.info("Found category {}", name);
            return template.queryForObject(sqlQuery, Long.class, name);
        } catch (EmptyResultDataAccessException e) {
            log.info("Category not found with name: {}", name);
            return null;
        }
    }

    private Long insertCategory(String name) {
        log.info("Inserting new category: {}", name);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
                .withTableName("categories")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = Map.of("name", name);
        log.info("Inserted new category with name: {}", name);
        return insert.executeAndReturnKey(values).longValue();
    }

    private void batchInsertNoteCategory(Long noteId, List<Long> categoryIds) {
        log.info("Batch inserting {} note-category relations for note id: {}", categoryIds.size(), noteId);
        String sqlQuery = """
                INSERT INTO note_category (note_id, category_id)
                VALUES (?, ?)
                """;
        List<Object[]> args = categoryIds.stream()
                .map(id -> new Object[]{noteId, id})
                .toList();
        template.batchUpdate(sqlQuery, args);
        log.info("Batch inserted for note-category relations");
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
            log.info("Tag not found with name: {}", name);
            return null;
        }
    }

    private Long insertTag(String name, String colour) {
        log.info("Inserting new tag: {} with colour: {}", name, colour);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
                .withTableName("tags")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = Map.of("name", name, "colour", colour);
        return insert.executeAndReturnKey(values).longValue();
    }

    private void batchInsertNoteTag(Long noteId, List<Long> tagIds) {
        log.info("Batch inserting {} note-tag relations for note id: {}", tagIds.size(), noteId);
        String sqlQuery = """
                INSERT INTO note_tag (note_id, tag_id)
                VALUES (?, ?)
                """;
        List<Object[]> args = tagIds.stream()
                .map(id -> new Object[]{noteId, id})
                .toList();
        template.batchUpdate(sqlQuery, args);
        log.info("Batch inserted for note-tag relations");
    }

    private List<Long> processCategories(List<CategoryDto> categoryNames) {
        log.info("Processing {} categories", categoryNames.size());
        List<Long> categoryIds = new ArrayList<>();
        for (CategoryDto categoryDto : categoryNames) {
            Long categoryId = findCategoryByName(categoryDto.getName());
            if (categoryId == null) {
                categoryId = insertCategory(categoryDto.getName());
            }
            categoryIds.add(categoryId);
        }
        return categoryIds;
    }

    private List<Long> processTags(List<TagDto> tags) {
        log.info("Processing {} tags", tags.size());
        List<Long> tagIds = new ArrayList<>();
        for (TagDto tag : tags) {
            Long tagId = findTagByName(tag.getName());
            if (tagId == null) {
                tagId = insertTag(tag.getName(), tag.getColour() == null ? "#000000" : tag.getColour());
            }
            tagIds.add(tagId);
        }
        return tagIds;
    }


    private static class NoteWithRelationsExtractor implements ResultSetExtractor<List<Note>> {
        @Override
        public List<Note> extractData(ResultSet rs) throws SQLException {
            Map<Long, Note> noteMap = new HashMap<>();

            while (rs.next()) {
                Long noteId = rs.getLong("note_id");
                Note note = noteMap.get(noteId);

                if (note == null) {
                    note = mapNote(rs);
                    note.setNoteCategories(new ArrayList<>());
                    note.setNoteTags(new ArrayList<>());
                    noteMap.put(noteId, note);
                }
                addCategoryToNote(rs, note);
                addTagToNote(rs, note);
            }

            return new ArrayList<>(noteMap.values());
        }

        private static Note mapNote(ResultSet rs) throws SQLException {
            Note n = new Note();
            n.setId(rs.getLong("note_id"));
            n.setName(rs.getString("note_name"));
            n.setDateOfCreation(rs.getObject("date_of_creation", LocalDate.class));
            n.setDateOfUpdate(rs.getObject("date_of_update", LocalDate.class));
            n.setIsDone(rs.getBoolean("is_done"));
            return n;
        }

        private void addCategoryToNote(ResultSet rs, Note note) throws SQLException {
            Long categoryId = rs.getLong("category_id");
            if (!rs.wasNull() && categoryId > 0) {
                boolean categoryExists = note.getNoteCategories().stream()
                        .anyMatch(nc -> nc.getCategory().getId().equals(categoryId));

                if (!categoryExists) {
                    Category category = new Category();
                    category.setId(categoryId);
                    category.setName(rs.getString("category_name"));

                    NoteCategory noteCategory = new NoteCategory();
                    noteCategory.setCategory(category);
                    noteCategory.setNote(note);
                    note.getNoteCategories().add(noteCategory);
                }
            }
        }

        private void addTagToNote(ResultSet rs, Note note) throws SQLException {
            Long tagId = rs.getLong("tag_id");
            if (!rs.wasNull() && tagId > 0) {
                boolean tagExists = note.getNoteTags().stream()
                        .anyMatch(nt -> nt.getTag().getId().equals(tagId));

                if (!tagExists) {
                    Tag tag = new Tag();
                    tag.setId(tagId);
                    tag.setName(rs.getString("tag_name"));
                    tag.setColour(rs.getString("tag_colour"));

                    NoteTag noteTag = new NoteTag();
                    noteTag.setTag(tag);
                    noteTag.setNote(note);
                    note.getNoteTags().add(noteTag);
                }
            }
        }
    }
}
