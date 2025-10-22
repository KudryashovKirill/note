package com.example.note.demo.service;

import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.model.Note;
import com.example.note.demo.repository.NoteRepository;
import com.example.note.demo.util.mapper.NoteMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CacheConfig(cacheNames = "notes")
public class NoteService {
    NoteRepository noteRepository;
    NoteMapper noteMapper;

    @Autowired
    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
    }

    @Caching(
            put = {
                    @CachePut(value = "#result.id")
            },
            evict = {
                    @CacheEvict(key = "'all'")
            }
    )
    public NoteDto save(NoteDto noteDto) {
        log.info("Service: Saving new note with name: {}", noteDto.getName());
        if (noteDto.getDateOfCreation() == null) {
            noteDto.setDateOfCreation(LocalDate.now());
            noteDto.setDateOfUpdate(LocalDate.now());
        }
        Note note = noteRepository.save(noteMapper.toEntity(noteDto), noteDto.getCategories(), noteDto.getTags());
        log.info("Service: Successfully saved note with id: {}", note.getId());
        return noteMapper.toDto(note);
    }

    @Cacheable(key = "#id")
    public NoteDto getById(Long id) {
        log.info("Service: Getting note by id: {}", id);
        Note note = noteRepository.getById(id);
        log.info("Service: Successfully get note with id: {} and {} categories, {} tags",
                id, note.getNoteCategories().size(), note.getNoteTags().size());
        return noteMapper.toDto(note);
    }

    @Cacheable(key = "'all'")
    public List<NoteDto> getAll() {
        log.debug("Service: Getting all notes");
        List<Note> notes = noteRepository.getAll();
        log.debug("Service: Retrieved {} notes", notes.size());
        return noteMapper.toDto(notes);
    }

    @Caching(put = {
            @CachePut(key = "#id")
    },
            evict = {
                    @CacheEvict(key = "'all'")
            }
    )
    public NoteDto update(NoteDto noteDto, Long id) {
        log.info("Service: Updating note with id: {}", id);
        Note note = noteRepository.update(noteMapper.toEntity(noteDto), id);
        log.info("Service: Successfully updated note with id: {}", id);
        return noteMapper.toDto(note);
    }

    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'all'")
    })
    public Map<String, Boolean> delete(Long id) {
        log.info("Service: Deleting note with id: {}", id);
        Map<String, Boolean> result = noteRepository.delete(id);
        log.info("Service: Note delete completed for id: {}, result: {}", id, result);
        return result;
    }
}
