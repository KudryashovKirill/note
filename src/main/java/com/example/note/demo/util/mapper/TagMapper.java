package com.example.note.demo.util.mapper;

import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {
    public Tag toEntity(TagDto dto) {
        if (dto == null) {
            return null;
        }

        return Tag.builder()
                .name(dto.getName())
                .colour(dto.getColour() != null ? dto.getColour() : "#000000")
                .build();
    }

    public TagDto toDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        return TagDto.builder()
                .name(tag.getName())
                .colour(tag.getColour())
                .build();
    }

}
