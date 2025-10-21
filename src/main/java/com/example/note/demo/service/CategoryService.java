package com.example.note.demo.service;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.model.Category;
import com.example.note.demo.model.Note;
import com.example.note.demo.repository.CategoryRepository;
import com.example.note.demo.repository.NoteCategoryRepository;
import com.example.note.demo.util.mapper.CategoryMapper;
import com.example.note.demo.util.mapper.NoteMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CacheConfig(cacheNames = "categories")
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    NoteCategoryRepository noteCategoryRepository;
    NoteMapper noteMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
                           NoteCategoryRepository noteCategoryRepository, NoteMapper noteMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.noteCategoryRepository = noteCategoryRepository;
        this.noteMapper = noteMapper;
    }

    @Caching(evict = {
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "notes", allEntries = true)
    })
    public CategoryDto save(CategoryDto categoryDto) {
        log.info("Service: Saving category with name: {}", categoryDto.getName());
        Category savedCategory = categoryRepository.save(categoryMapper.toEntity(categoryDto));
        log.info("Service: Successfully saved category with id: {}", savedCategory.getId());
        return categoryMapper.toDto(savedCategory);
    }

    @Cacheable(key = "#id")
    public CategoryDto getById(Long id) {
        log.info("Service: Getting category by id: {}", id);
        Category category = categoryRepository.getById(id);
        log.info("Service: Successfully get category: {}", category);
        return categoryMapper.toDto(category);
    }

    @Cacheable
    public List<CategoryDto> getAll() {
        log.info("Service: Getting all categories");
        List<Category> categories = categoryRepository.getAll();
        log.info("Service: get {} categories", categories.size());
        return categoryMapper.toDto(categories);
    }

    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "notes", allEntries = true)
    })
    public CategoryDto update(CategoryDto categoryDto, Long id) {
        log.info("Service: Updating category with id: {} to name: {}", id, categoryDto.getName());
        Category updatedCategory = categoryRepository.update(categoryMapper.toEntity(categoryDto), id);
        log.info("Service: Successfully updated category with id: {}", id);
        return categoryMapper.toDto(categoryRepository.update(updatedCategory, id));
    }

    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "notes", allEntries = true)
    })
    public Map<String, Boolean> delete(Long id) {
        log.info("Service: Deleting category with id: {}", id);
        Map<String, Boolean> result = categoryRepository.delete(id);
        log.info("Service: Category deletion completed for id: {}, result: {}", id, result);
        return result;
    }

    public NoteDto addCategoryToNote(Long noteId, Long categoryId) {
        log.info("Service: Adding category id: {} to note id: {}", categoryId, noteId);
        Note note = noteCategoryRepository.addCategoryToNote(noteId, categoryId);
        log.info("Service: Successfully added category to note");
        return noteMapper.toDto(note);
    }

    public NoteDto updateCategoryInNote(Long noteId, Long categoryId, Long newCategoryId) {
        log.info("Service: Updating category in note: noteId={}, oldCategoryId={}, newCategoryId={}",
                noteId, categoryId, newCategoryId);
        Note note = noteCategoryRepository.updateCategoryInNote(noteId, categoryId, newCategoryId);
        log.info("Service: Successfully updated category in note");
        return noteMapper.toDto(note);
    }

    public Map<String, Boolean> deleteCategoryFromNote(Long noteId, Long categoryId) {
        log.info("Service: Deleting category id: {} from note id: {}", categoryId, noteId);
        Map<String, Boolean> result = noteCategoryRepository.deleteCategoryFromNote(noteId, categoryId);
        log.info("Service: Category deletion from note completed, result: {}", result);
        return result;
    }
}
