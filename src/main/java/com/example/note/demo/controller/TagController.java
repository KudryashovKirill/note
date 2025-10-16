package com.example.note.demo.controller;

import com.example.note.demo.dto.TagDto;
import com.example.note.demo.service.TagService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tag")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = {"http://127.0.0.1:5533", "http://localhost:5533"})
public class TagController {
    TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagDto> save(@RequestBody TagDto tagDto) {
        return new ResponseEntity<>(tagService.save(tagDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(tagService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TagDto>> getAll() {
        return new ResponseEntity<>(tagService.getAll(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> update(@RequestBody TagDto tagDto, @PathVariable Long id) {
        return new ResponseEntity<>(tagService.update(tagDto, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(tagService.delete(id), HttpStatus.OK);
    }

    @PostMapping("/{tagId}/note/{noteId}")
    public ResponseEntity<?> addTagToNote(@PathVariable Long noteId, @PathVariable Long tagId) {
        return new ResponseEntity<>(tagService.addTagToNote(noteId, tagId), HttpStatus.OK);
    }

    @PutMapping("/{tagId}/note/{noteId}/newTag/{newTagId}")
    public ResponseEntity<?> updateTagInNote(
            @PathVariable Long noteId,
            @PathVariable Long tagId,
            @PathVariable Long newTagId) {
        return new ResponseEntity<>(tagService.updateTagInNote(noteId, tagId, newTagId), HttpStatus.OK);
    }

    @DeleteMapping("/{tagId}/note/{noteId}")
    public ResponseEntity<Map<String, Boolean>> deleteTagFromNote(
            @PathVariable Long noteId,
            @PathVariable Long tagId) {
        return new ResponseEntity<>(tagService.deleteTagFromNote(noteId, tagId), HttpStatus.OK);
    }
}
