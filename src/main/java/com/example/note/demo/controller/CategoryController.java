package com.example.note.demo.controller;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = {"http://127.0.0.1:5533", "http://localhost:5533"})
public class CategoryController {
    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> save(@RequestBody @Valid CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.save(categoryDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(categoryService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll() {
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@RequestBody @Valid CategoryDto categoryDto, @PathVariable Long id) {
        return new ResponseEntity<>(categoryService.update(categoryDto, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(categoryService.delete(id), HttpStatus.OK);
    }

    @PostMapping("/{categoryId}/note/{noteId}")
    public ResponseEntity<NoteDto> addCategoryToNote(@PathVariable Long noteId, @PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.addCategoryToNote(noteId, categoryId), HttpStatus.OK);
    }

    @PutMapping("/{categoryId}/note/{noteId}/newCategory/{newCategoryId}")
    public ResponseEntity<NoteDto> updateCategoryInNote(@PathVariable Long noteId, @PathVariable Long categoryId,
                                                        @PathVariable Long newCategoryId) {
        return new ResponseEntity<>(categoryService.updateCategoryInNote(noteId, categoryId, newCategoryId),
                HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}/note/{noteId}")
    public ResponseEntity<Map<String, Boolean>> deleteCategoryFromNote(@PathVariable Long noteId,
                                                                       @PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.deleteCategoryFromNote(noteId, categoryId), HttpStatus.OK);
    }
}
