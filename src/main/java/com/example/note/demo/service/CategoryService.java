package com.example.note.demo.service;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.model.Category;
import com.example.note.demo.repository.CategoryRepository;
import com.example.note.demo.repository.NoteCategoryRepository;
import com.example.note.demo.util.mapper.CategoryMapper;
import com.example.note.demo.util.mapper.NoteMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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

    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(categoryRepository.getById(id));
    }

    public List<CategoryDto> getAll() {
        return categoryMapper.toDto(categoryRepository.getAll());
    }

    public CategoryDto update(CategoryDto categoryDto, Long id) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.update(category, id));
    }

    public Map<String, Boolean> delete(Long id) {
        return categoryRepository.delete(id);
    }

    public NoteDto addCategoryToNote(Long noteId, Long categoryId) {
        return noteMapper.toDto(noteCategoryRepository.addCategoryToNote(noteId, categoryId));
    }

    public NoteDto updateCategoryInNote(Long noteId, Long categoryId, Long newCategoryId) {
        return noteMapper.toDto(noteCategoryRepository.updateCategoryInNote(noteId, categoryId, newCategoryId));
    }

    public Map<String, Boolean> deleteCategoryFromNote(Long noteId, Long categoryId) {
        return noteCategoryRepository.deleteCategoryFromNote(noteId, categoryId);
    }
}
