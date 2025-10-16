package com.example.note.demo.service;

import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.model.Note;
import com.example.note.demo.repository.NoteRepository;
import com.example.note.demo.util.mapper.NoteMapper;
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
    NoteMapper noteMapper;

    @Autowired
    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
    }

    public NoteDto save(NoteDto noteDto) {
        Note note = noteMapper.toEntity(noteDto);
        return noteMapper.toDto(noteRepository.save(note, noteDto.getCategories(), noteDto.getTags()));
    }

    public NoteDto getById(Long id) {
        return noteMapper.toDto(noteRepository.getById(id));
    }

    public List<NoteDto> getAll() {
        return noteMapper.toDto(noteRepository.getAll());
    }

    public NoteDto update(NoteDto noteDto, Long id) {
        Note note = noteMapper.toEntity(noteDto);
        return noteMapper.toDto(noteRepository.update(note, id));
    }

    public Map<String, Boolean> delete(Long id) {
        return noteRepository.delete(id);
    }
}
