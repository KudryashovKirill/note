package com.example.note.demo.util.mapper;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.Category;
import com.example.note.demo.model.Note;
import com.example.note.demo.model.NoteCategory;
import com.example.note.demo.model.Tag;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class NoteMapper {
    public NoteDto toDto(Note note) {
        return NoteDto.builder()
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
                .name(category.getName())
                .build();
    }

    private TagDto toDto(Tag tag) {
        return TagDto.builder()
                .name(tag.getName())
                .colour(tag.getColour())
                .build();
    }

    public Note toEntity(NoteDto dto) {
        if (dto == null) return null;

        return Note.builder()
                .name(dto.getName())
                .dateOfCreation(LocalDate.now())
                .dateOfUpdate(LocalDate.now())
                .isDone(dto.getIsDone())
                .build();
    }

    public List<NoteDto> toDto(List<Note> note) {
        return note.stream()
                .map(this::toDto)
                .toList();
    }

    public List<Note> toEntity(List<NoteDto> dto) {
        if (dto == null) return null;

        return dto.stream()
                .map(this::toEntity)
                .toList();
    }

    private NoteCategory toNoteCategory(CategoryDto dto) {
        return NoteCategory.builder()
                .build();
    }
}


//Long id;
//    @Column(name = "name", length = 100, nullable = false)
//    String name;
//    @Column(name = "date_of_creation", nullable = false)
//    LocalDate dateOfCreation;
//    @Column(name = "date_of_update", nullable = false)
//    LocalDate dateOfUpdate;
//    @Column(name = "is_done", nullable = false)
//    Boolean isDone;

//String name;
//    LocalDate dateOfCreation;
//    LocalDate dateOfUpdate;
//    Boolean isDone;
//    List<Long> categoryIds;
//    List<TagDto> tags;