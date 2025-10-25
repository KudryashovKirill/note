package com.example.note.demo.controller;

import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.service.NoteService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/note")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoteController {
    NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteDto> save(@RequestBody NoteDto noteDto) {
        log.info("Received POST request to create note with name: {}", noteDto.getName());
        NoteDto savedNote = noteService.save(noteDto);
        log.info("Successfully created note with name: {}", savedNote.getName());
        return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDto> getById(@PathVariable Long id) {
        log.info("Received GET request to fetch note by ID: {}", id);
        NoteDto note = noteService.getById(id);
        log.info("Successfully retrieved note with ID: {} and name: {}", id, note.getName());
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<NoteDto>> getAll() {
        log.info("Received GET request to fetch all notes");
        List<NoteDto> notes = noteService.getAll();
        log.info("Successfully retrieved {} notes", notes.size());
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> update(@RequestBody NoteDto noteDto, @PathVariable Long id) {
        log.info("Received PUT request to update note with ID: {}", id);
        NoteDto updatedNote = noteService.update(noteDto, id);
        log.info("Successfully updated note with ID: {} to name: {}", id, updatedNote.getName());
        return new ResponseEntity<>(updatedNote, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id) {
        log.info("Received DELETE request to remove note with ID: {}", id);
        Map<String, Boolean> result = noteService.delete(id);
        log.info("Successfully processed DELETE request for note ID: {}, result: {}", id, result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
