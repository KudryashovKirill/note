package com.example.note.demo.util.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorResponse {
    String name;
    String description;
    LocalDateTime time = LocalDateTime.now();

    public ErrorResponse(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
