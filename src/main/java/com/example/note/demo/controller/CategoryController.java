package com.example.note.demo.controller;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.service.CategoryService;
import jakarta.validation.Valid;
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
@RequestMapping("/category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> save(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Received POST request to create category with name: {}", categoryDto.getName());
        CategoryDto savedCategory = categoryService.save(categoryDto);
        log.info("Successfully created category with name: {}", savedCategory.getName());
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        log.info("Received GET request to fetch category by ID: {}", id);
        CategoryDto category = categoryService.getById(id);
        log.info("Successfully retrieved category with ID: {} and name: {}", id, category.getName());
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll() {
        log.info("Received GET request to fetch all categories");
        List<CategoryDto> categories = categoryService.getAll();
        log.info("Successfully retrieved {} categories", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@RequestBody @Valid CategoryDto categoryDto, @PathVariable Long id) {
        log.info("Received PUT request to update category with ID: {} to new name: {}", id, categoryDto.getName());
        CategoryDto updatedCategory = categoryService.update(categoryDto, id);
        log.info("Successfully updated category with ID: {} to name: {}", id, updatedCategory.getName());
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id) {
        log.info("Received DELETE request to remove category with ID: {}", id);
        Map<String, Boolean> result = categoryService.delete(id);
        log.info("Successfully processed DELETE request for category ID: {}, result: {}", id, result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{categoryId}/note/{noteId}")
    public ResponseEntity<NoteDto> addCategoryToNote(@PathVariable Long noteId, @PathVariable Long categoryId) {
        log.info("Received POST request to add category ID: {} to note ID: {}", categoryId, noteId);
        NoteDto note = categoryService.addCategoryToNote(noteId, categoryId);
        log.info("Successfully added category ID: {} to note ID: {}", categoryId, noteId);
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    @PutMapping("/{categoryId}/note/{noteId}/newCategory/{newCategoryId}")
    public ResponseEntity<NoteDto> updateCategoryInNote(@PathVariable Long noteId, @PathVariable Long categoryId,
                                                        @PathVariable Long newCategoryId) {
        log.info("Received PUT request to update category in note - Note ID: {}, Old Category ID: {}, New Category ID: {}",
                noteId, categoryId, newCategoryId);
        NoteDto note = categoryService.updateCategoryInNote(noteId, categoryId, newCategoryId);
        log.info("Successfully updated category in note - Note ID: {}, Old Category ID: {}, New Category ID: {}",
                noteId, categoryId, newCategoryId);
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}/note/{noteId}")
    public ResponseEntity<Map<String, Boolean>> deleteCategoryFromNote(@PathVariable Long noteId,
                                                                       @PathVariable Long categoryId) {
        log.info("Received DELETE request to remove category ID: {} from note ID: {}", categoryId, noteId);
        Map<String, Boolean> result = categoryService.deleteCategoryFromNote(noteId, categoryId);
        log.info("Successfully processed DELETE request for category ID: {} from note ID: {}, result: {}",
                categoryId, noteId, result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
