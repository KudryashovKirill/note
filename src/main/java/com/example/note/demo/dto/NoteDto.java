package com.example.note.demo.dto;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteDto {
    String name;
    LocalDate dateOfCreation;
    LocalDate dateOfUpdate;
    Boolean isDone;
    List<CategoryDto> categories;
    List<TagDto> tags;
}

