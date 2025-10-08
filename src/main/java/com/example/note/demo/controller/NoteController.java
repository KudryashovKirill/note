package com.example.note.demo.controller;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.dto.TagDto;
import com.example.note.demo.service.NoteService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoteController {
    NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteDto> save(@RequestBody NoteDto noteDto) {
        return new ResponseEntity<>(noteService.save(noteDto,
                noteDto.getCategories() == null ? List.of() : noteDto.getCategories(),
                noteDto.getTags() == null ? List.of() : noteDto.getTags()),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(noteService.getById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> update(@RequestBody NoteDto noteDto, @PathVariable Long id) {
        return new ResponseEntity<>(noteService.update(noteDto, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(noteService.delete(id), HttpStatus.OK);
    }
}
