package com.example.note.demo.repository;

import com.example.note.demo.model.Note;
import com.example.note.demo.util.exception.NoDataFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Slf4j
@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoteCategoryRepository {
    JdbcTemplate template;

    @Autowired
    public NoteCategoryRepository(JdbcTemplate template) {
        this.template = template;
    }

    @Transactional
    public Note addCategoryToNote(Long noteId, Long categoryId) {
        log.info("Adding category id: {} to note id: {}", categoryId, noteId);
        Note noteInTable = checkNoteInTable(noteId);
        checkCategoryInTable(categoryId);

        String insertSql = "INSERT INTO note_category (note_id, category_id) VALUES (?, ?)";
        template.update(insertSql, noteId, categoryId);
        log.info("Successfully added category to note");
        return noteInTable;
    }

    @Transactional
    public Note updateCategoryInNote(Long noteId, Long categoryId, Long newCategoryId) {
        log.info("Updating category in note: noteId={}, oldCategoryId={}, newCategoryId={}",
                noteId, categoryId, newCategoryId);
        Note noteInTable = checkNoteInTable(noteId);
        checkCategoryInTable(categoryId);
        checkCategoryInTable(newCategoryId);

        String sqlQuery = """
                UPDATE note_category
                SET category_id = ?
                WHERE note_id = ? AND category_id = ?
                """;
        template.update(sqlQuery, newCategoryId, noteId, categoryId);
        log.info("Updated category in note");
        return noteInTable;
    }

    public Map<String, Boolean> deleteCategoryFromNote(Long noteId, Long categoryId) {
        log.info("Deleting category id: {} from note id: {}", categoryId, noteId);
        checkNoteCategoryInTable(noteId, categoryId);
        String sqlQuery = """
                DELETE FROM note_category
                WHERE note_id = ? AND category_id = ?
                """;
        int countOfDeleted = template.update(sqlQuery, noteId, categoryId);
        log.info("Category deletion from note result: {}", countOfDeleted > 0);
        return Map.of("deleted", countOfDeleted > 0);
    }

    private Note checkNoteInTable(Long noteId) {
        log.info("Checking note exists with id: {}", noteId);
        try {
            String sqlNote = "SELECT * FROM notes WHERE id = ?";
            return template.queryForObject(sqlNote, (rs, rowNum) -> {
                Note note = new Note();
                note.setId(rs.getLong("id"));
                note.setName(rs.getString("name"));
                note.setDateOfCreation(rs.getDate("date_of_creation").toLocalDate());
                note.setDateOfUpdate(rs.getDate("date_of_update").toLocalDate());
                note.setIsDone(rs.getBoolean("is_done"));
                return note;
            }, noteId);
        } catch (EmptyResultDataAccessException e) {
            log.error("No note found with id: {}", noteId);
            throw new NoDataFoundException("No note found by id " + noteId);
        }
    }

    private void checkCategoryInTable(Long categoryId) {
        log.info("Checking category exists with id: {}", categoryId);
        try {
            template.queryForObject("SELECT COUNT(*) FROM categories WHERE id = ?", Integer.class, categoryId);
        } catch (EmptyResultDataAccessException e) {
            log.error("No category found with id: {}", categoryId);
            throw new NoDataFoundException("No category found by id = " + categoryId);
        }
    }

    private void checkNoteCategoryInTable(Long noteId, Long categoryId) {
        log.info("Checking note-category relation: noteId={}, categoryId={}", noteId, categoryId);
        Integer count = template.queryForObject(
                "SELECT COUNT(*) FROM note_category WHERE note_id = ? AND category_id = ?",
                Integer.class, noteId, categoryId
        );
        if (count == null || count == 0) {
            log.error("No note-category relation found: noteId={}, categoryId={}", noteId, categoryId);
            throw new NoDataFoundException("No note_id= " + noteId + " and category_id= " + categoryId + " found");
        }
    }
}
