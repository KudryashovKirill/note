package com.example.note.demo.util.mapper;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryDto toDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .name(category.getName())
                .build();
    }

    public Category toEntity(CategoryDto dto) {
        if (dto == null) return null;

        return Category.builder()
                .name(dto.getName())
                .build();
    }
}
