package com.example.note.demo.service;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.model.Category;
import com.example.note.demo.model.Note;
import com.example.note.demo.repository.CategoryRepository;
import com.example.note.demo.repository.NoteCategoryRepository;
import com.example.note.demo.util.CategoryMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper mapper;
    NoteCategoryRepository noteCategoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper mapper, NoteCategoryRepository noteCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.noteCategoryRepository = noteCategoryRepository;
    }

    public CategoryDto save(CategoryDto categoryDto) {
        Category category = mapper.toEntity(categoryDto);
        return mapper.toDto(categoryRepository.save(category));
    }

    public Category getById(Long id) {
        return categoryRepository.getById(id);
    }

    public CategoryDto update(CategoryDto categoryDto, Long id) {
        Category category = mapper.toEntity(categoryDto);
        return mapper.toDto(categoryRepository.update(category, id));
    }

    public Map<String, Boolean> delete(Long id) {
        return categoryRepository.delete(id);
    }

    public Note addCategoryToNote(Long noteId, Long categoryId) {
        return noteCategoryRepository.addCategoryToNote(noteId, categoryId);
    }

    public Note updateCategoryInNote(Long noteId, Long categoryId, Long newCategoryId) {
        return noteCategoryRepository.updateCategoryInNote(noteId, categoryId, newCategoryId);
    }

    public Map<String, Boolean> deleteCategoryFromNote(Long noteId, Long categoryId) {
        return noteCategoryRepository.deleteCategoryFromNote(noteId, categoryId);
    }
}
