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
public class NoteTagRepository {
    JdbcTemplate template;

    @Autowired
    public NoteTagRepository(JdbcTemplate template) {
        this.template = template;
    }

    @Transactional
    public Note addTagToNote(Long noteId, Long tagId) {
        log.info("Adding tag id: {} to note id: {}", tagId, noteId);
        Note noteInTable = checkNoteInTable(noteId);
        checkTagInTable(tagId);

        String insertSql = "INSERT INTO note_tag (note_id, tag_id) VALUES (?, ?)";
        int insertedRows = template.update(insertSql, noteId, tagId);
        log.info("Successfully added tag to note. Inserted {} rows", insertedRows);
        return noteInTable;
    }

    @Transactional
    public Note updateTagInNote(Long noteId, Long oldTagId, Long newTagId) {
        log.info("Updating tag in note: noteId={}, oldTagId={}, newTagId={}", noteId, oldTagId, newTagId);
        Note noteInTable = checkNoteInTable(noteId);
        checkTagInTable(oldTagId);
        checkTagInTable(newTagId);

        String sqlQuery = """
                UPDATE note_tag
                SET tag_id = ?
                WHERE note_id = ? AND tag_id = ?
                """;
        int updatedRows = template.update(sqlQuery, newTagId, noteId, oldTagId);
        log.info("Updated {} rows for tag update in note", updatedRows);
        return noteInTable;
    }

    @Transactional
    public Map<String, Boolean> deleteTagFromNote(Long noteId, Long tagId) {
        log.info("Deleting tag id: {} from note id: {}", tagId, noteId);
        checkNoteTagInTable(noteId, tagId);
        String sqlQuery = """
                DELETE FROM note_tag
                WHERE note_id = ? AND tag_id = ?
                """;
        int countOfUpdate = template.update(sqlQuery, noteId, tagId);
        log.info("Tag deletion from note result: {}", countOfUpdate > 0);
        return Map.of("deleted", countOfUpdate > 0);
    }

    private Note checkNoteInTable(Long noteId) {
        log.info("Checking note exists with id: {}", noteId);
        try {
            return template.queryForObject(
                    "SELECT * FROM notes WHERE id = ?",
                    (rs, rowNum) -> {
                        Note note = new Note();
                        note.setId(rs.getLong("id"));
                        note.setName(rs.getString("name"));
                        note.setDateOfCreation(rs.getDate("date_of_creation").toLocalDate());
                        note.setDateOfUpdate(rs.getDate("date_of_update").toLocalDate());
                        note.setIsDone(rs.getBoolean("is_done"));
                        return note;
                    },
                    noteId
            );
        } catch (EmptyResultDataAccessException e) {
            log.error("No note found with id: {}", noteId);
            throw new NoDataFoundException("No note found by id " + noteId);
        }
    }

    private void checkTagInTable(Long tagId) {
        log.info("Checking tag exists with id: {}", tagId);
        Integer count = template.queryForObject(
                "SELECT COUNT(*) FROM tags WHERE id = ?", Integer.class, tagId
        );
        if (count == null || count == 0) {
            log.error("No tag found with id: {}", tagId);
            throw new NoDataFoundException("No tag found by id = " + tagId);
        }
        log.info("Tag exists with id: {}", tagId);
    }

    private void checkNoteTagInTable(Long noteId, Long tagId) {
        log.info("Checking note-tag relation: noteId={}, tagId={}", noteId, tagId);
        Integer count = template.queryForObject(
                "SELECT COUNT(*) FROM note_tag WHERE note_id = ? AND tag_id = ?",
                Integer.class, noteId, tagId
        );
        if (count == null || count == 0) {
            log.error("No note-tag relation found: noteId={}, tagId={}", noteId, tagId);
            throw new NoDataFoundException("No relation found: note_id=" + noteId + ", tag_id=" + tagId);
        }
        log.info("Note-tag relation exists: noteId={}, tagId={}", noteId, tagId);
    }
}
