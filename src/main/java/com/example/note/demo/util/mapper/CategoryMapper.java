package com.example.note.demo.util.mapper;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.model.Category;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CategoryMapper {
    public CategoryDto toDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toEntity(CategoryDto dto) {
        if (dto == null) return null;

        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public List<CategoryDto> toDto(List<Category> category) {
        if (category == null) {
            return Collections.emptyList();
        }
        return category.stream()
                .map(this::toDto)
                .toList();
    }

    public List<Category> toEntity(List<CategoryDto> dto) {
        if (dto == null) {
            return Collections.emptyList();
        }

        return dto.stream()
                .map(this::toEntity)
                .toList();
    }
}
