package com.example.note.demo.controller;

import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.dto.TagDto;
import com.example.note.demo.service.TagService;
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
@RequestMapping("/tag")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = {"http://127.0.0.1:5533", "http://localhost:5533", "http://127.0.0.1:5500", "http://localhost:5500"})
public class TagController {
    TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagDto> save(@RequestBody TagDto tagDto) {
        log.info("Received POST request to create tag with name: {} and colour: {}",
                tagDto.getName(), tagDto.getColour());
        TagDto savedTag = tagService.save(tagDto);
        log.info("Successfully created tag with name: {} and colour: {}",
                savedTag.getName(), savedTag.getColour());
        return new ResponseEntity<>(savedTag, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getById(@PathVariable Long id) {
        log.info("Received GET request to fetch tag by ID: {}", id);
        TagDto tag = tagService.getById(id);
        log.info("Successfully retrieved tag with ID: {}, name: {}, colour: {}",
                id, tag.getName(), tag.getColour());
        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TagDto>> getAll() {
        log.info("Received GET request to fetch all tags");
        List<TagDto> tags = tagService.getAll();
        log.info("Successfully retrieved {} tags", tags.size());
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> update(@RequestBody TagDto tagDto, @PathVariable Long id) {
        log.info("Received PUT request to update tag with ID: {} to name: {} and colour: {}",
                id, tagDto.getName(), tagDto.getColour());
        TagDto updatedTag = tagService.update(tagDto, id);
        log.info("Successfully updated tag with ID: {} to name: {} and colour: {}",
                id, updatedTag.getName(), updatedTag.getColour());
        return new ResponseEntity<>(updatedTag, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id) {
        log.info("Received DELETE request to remove tag with ID: {}", id);
        Map<String, Boolean> result = tagService.delete(id);
        log.info("Successfully processed DELETE request for tag ID: {}, result: {}", id, result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{tagId}/note/{noteId}")
    public ResponseEntity<?> addTagToNote(@PathVariable Long noteId, @PathVariable Long tagId) {
        log.info("Received POST request to add tag ID: {} to note ID: {}", tagId, noteId);
        NoteDto note = tagService.addTagToNote(noteId, tagId);
        log.info("Successfully added tag ID: {} to note ID: {}", tagId, noteId);
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    @PutMapping("/{tagId}/note/{noteId}/newTag/{newTagId}")
    public ResponseEntity<?> updateTagInNote(
            @PathVariable Long noteId,
            @PathVariable Long tagId,
            @PathVariable Long newTagId) {
        log.info("Received PUT request to update tag in note - Note ID: {}, Old Tag ID: {}, New Tag ID: {}",
                noteId, tagId, newTagId);
        NoteDto note = tagService.updateTagInNote(noteId, tagId, newTagId);
        log.info("Successfully updated tag in note - Note ID: {}, Old Tag ID: {}, New Tag ID: {}",
                noteId, tagId, newTagId);
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    @DeleteMapping("/{tagId}/note/{noteId}")
    public ResponseEntity<Map<String, Boolean>> deleteTagFromNote(
            @PathVariable Long noteId,
            @PathVariable Long tagId) {
        log.info("Received DELETE request to remove tag ID: {} from note ID: {}", tagId, noteId);
        Map<String, Boolean> result = tagService.deleteTagFromNote(noteId, tagId);
        log.info("Successfully processed DELETE request for tag ID: {} from note ID: {}, result: {}",
                tagId, noteId, result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
