package com.example.note.demo.util.mapper;

import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.Tag;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TagMapper {
    public Tag toEntity(TagDto dto) {
        if (dto == null) {
            return null;
        }

        return Tag.builder()
                .id(dto.getId())
                .name(dto.getName())
                .colour(dto.getColour() != null ? dto.getColour() : "#000000")
                .build();
    }

    public TagDto toDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .colour(tag.getColour())
                .build();
    }

    public List<TagDto> toDto(List<Tag> tag) {
        if (tag == null) {
            return Collections.emptyList();
        }
        return tag.stream()
                .map(this::toDto)
                .toList();
    }

    public List<Tag> toEntity(List<TagDto> dto) {
        if (dto == null) {
            return Collections.emptyList();
        }

        return dto.stream()
                .map(this::toEntity)
                .toList();
    }
}
