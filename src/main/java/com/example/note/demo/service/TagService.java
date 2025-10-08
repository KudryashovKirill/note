package com.example.note.demo.service;

import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.Note;
import com.example.note.demo.model.Tag;
import com.example.note.demo.repository.NoteTagRepository;
import com.example.note.demo.repository.TagRepository;
import com.example.note.demo.util.TagMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagService {
    TagRepository tagRepository;
    TagMapper mapper;
    NoteTagRepository noteTagRepository;

    @Autowired
    public TagService(TagRepository tagRepository, TagMapper mapper, NoteTagRepository noteTagRepository) {
        this.tagRepository = tagRepository;
        this.mapper = mapper;
        this.noteTagRepository = noteTagRepository;
    }

    public TagDto save(TagDto tagDto) {
        Tag tag = mapper.toEntity(tagDto);
        return mapper.toDto(tagRepository.save(tag));
    }

    public Tag getById(Long id) {
        return tagRepository.getById(id);
    }

    public TagDto update(TagDto tagDto, Long id) {
        Tag tag = mapper.toEntity(tagDto);
        return mapper.toDto(tagRepository.update(tag, id));
    }

    public Map<String, Object> delete(Long id) {
        return tagRepository.delete(id);
    }

    public Note addTagToNote(Long noteId, Long tagId) {
        return noteTagRepository.addTagToNote(noteId, tagId);
    }

    public Note addTagToNoteByName(Long noteId, String tagName, String colour) {
        return noteTagRepository.addTagToNoteByName(noteId, tagName, colour);
    }

    public Note updateTagInNote(Long noteId, Long oldTagId, Long newTagId) {
        return noteTagRepository.updateTagInNote(noteId, oldTagId, newTagId);
    }

    public Map<String, Boolean> deleteTagFromNote(Long noteId, Long tagId) {
        return noteTagRepository.deleteTagFromNote(noteId, tagId);
    }
}
