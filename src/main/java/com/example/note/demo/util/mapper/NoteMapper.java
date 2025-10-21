package com.example.note.demo.util.mapper;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.Category;
import com.example.note.demo.model.Note;
import com.example.note.demo.model.Tag;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class NoteMapper {
    public NoteDto toDto(Note note) {
        return NoteDto.builder()
                .id(note.getId())
                .name(note.getName())
                .dateOfCreation(LocalDate.now())
                .dateOfUpdate(LocalDate.now())
                .isDone(note.getIsDone())
                .categories(note.getNoteCategories() != null ?
                        note.getNoteCategories().stream()
                                .map(nc -> toDto(nc.getCategory()))
                                .toList() : null)
                .tags(note.getNoteTags() != null ?
                        note.getNoteTags().stream()
                                .map(nt -> toDto(nt.getTag()))
                                .toList() : null)
                .build();
    }


    private CategoryDto toDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    private TagDto toDto(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .colour(tag.getColour())
                .build();
    }

    public Note toEntity(NoteDto dto) {
        if (dto == null) return null;

        return Note.builder()
                .id(dto.getId())
                .name(dto.getName())
                .dateOfCreation(LocalDate.now())
                .dateOfUpdate(LocalDate.now())
                .isDone(dto.getIsDone())
                .build();
    }


    public List<NoteDto> toDto(List<Note> note) {
        if (note == null) {
            return Collections.emptyList();
        }
        return note.stream()
                .map(this::toDto)
                .toList();
    }

    public List<Note> toEntity(List<NoteDto> dto) {
        if (dto == null) {
            return Collections.emptyList();
        }

        return dto.stream()
                .map(this::toEntity)
                .toList();
    }
}