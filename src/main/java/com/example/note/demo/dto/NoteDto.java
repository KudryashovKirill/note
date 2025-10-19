package com.example.note.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    Long id;
    @NotBlank(message = "Название заметки не может быть пустым")
    String name;
    @NotNull(message = "Дата создания обязательна")
    LocalDate dateOfCreation;
    @NotNull(message = "Статус выполнения обязателен")
    LocalDate dateOfUpdate;
    Boolean isDone;
    @NotNull(message = "Список категорий обязателен")
    @Size(min = 0, message = "Список категорий должен быть корректным")
    List<CategoryDto> categories;
    @NotNull(message = "Список тегов обязателен")
    @Size(min = 0, message = "Список тегов должен быть корректным")
    List<TagDto> tags;
}

