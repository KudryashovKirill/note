package com.example.note.demo.repository;

import com.example.note.demo.model.Note;
import com.example.note.demo.model.Tag;
import com.example.note.demo.util.exception.NoDataFoundException;
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
        Note noteInTable = checkNoteInTable(noteId);
        checkTagInTable(tagId);

        String insertSql = "INSERT INTO note_tag (note_id, tag_id) VALUES (?, ?)";
        template.update(insertSql, noteId, tagId);

        return noteInTable;
    }

    @Transactional
    public Note updateTagInNote(Long noteId, Long oldTagId, Long newTagId) {
        Note noteInTable = checkNoteInTable(noteId);
        checkTagInTable(oldTagId);
        checkTagInTable(newTagId);

        String sqlQuery = """
                UPDATE note_tag
                SET tag_id = ?
                WHERE note_id = ? AND tag_id = ?
                """;
        template.update(sqlQuery, newTagId, noteId, oldTagId);
        return noteInTable;
    }

    @Transactional
    public Map<String, Boolean> deleteTagFromNote(Long noteId, Long tagId) {
        checkNoteTagInTable(noteId, tagId);
        String sqlQuery = """
                DELETE FROM note_tag
                WHERE note_id = ? AND tag_id = ?
                """;
        int deleted = template.update(sqlQuery, noteId, tagId);
        return Map.of("deleted", deleted > 0);
    }

    private Note checkNoteInTable(Long noteId) {
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
            throw new NoDataFoundException("No note found by id " + noteId);
        }
    }

    private void checkTagInTable(Long tagId) {
        Integer count = template.queryForObject(
                "SELECT COUNT(*) FROM tag WHERE id = ?", Integer.class, tagId
        );
        if (count == null || count == 0) {
            throw new NoDataFoundException("No tag found by id = " + tagId);
        }
    }

    private void checkNoteTagInTable(Long noteId, Long tagId) {
        Integer count = template.queryForObject(
                "SELECT COUNT(*) FROM note_tag WHERE note_id = ? AND tag_id = ?",
                Integer.class, noteId, tagId
        );
        if (count == null || count == 0) {
            throw new NoDataFoundException("No relation found: note_id=" + noteId + ", tag_id=" + tagId);
        }
    }
}
