package com.example.note.demo.service;

import com.example.note.demo.dto.CategoryDto;
import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.Note;
import com.example.note.demo.repository.NoteRepository;
import com.example.note.demo.util.NoteMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoteService {
    NoteRepository noteRepository;
    NoteMapper mapper;

    @Autowired
    public NoteService(NoteRepository noteRepository, NoteMapper mapper) {
        this.noteRepository = noteRepository;
        this.mapper = mapper;
    }

    public NoteDto save(NoteDto noteDto, List<CategoryDto> categoryNames, List<TagDto> tags) {
        Note note = mapper.toEntity(noteDto);
        return mapper.toDto(noteRepository.save(note, categoryNames, tags));
    }

    public Note getById(Long id) {
        return noteRepository.getById(id);
    }

    public NoteDto update(NoteDto noteDto, Long id) {
        Note note = mapper.toEntity(noteDto);
        return mapper.toDto(noteRepository.update(note, id));
    }

    public Map<String, Boolean> delete(Long id) {
        return noteRepository.delete(id);
    }
}
