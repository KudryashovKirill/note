package com.example.note.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagDto {
    Long id;
    @NotBlank(message = "Название тега не может быть пустым")
    String name;
    @NotBlank(message = "Цвет тега не может быть пустым")
    String colour;
}
