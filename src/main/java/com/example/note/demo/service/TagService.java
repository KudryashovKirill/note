package com.example.note.demo.service;

import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.Tag;
import com.example.note.demo.repository.NoteTagRepository;
import com.example.note.demo.repository.TagRepository;
import com.example.note.demo.util.mapper.NoteMapper;
import com.example.note.demo.util.mapper.TagMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagService {
    TagRepository tagRepository;
    TagMapper tagMapper;
    NoteTagRepository noteTagRepository;
    NoteMapper noteMapper;

    @Autowired
    public TagService(TagRepository tagRepository, TagMapper tagMapper, NoteTagRepository noteTagRepository,
                      NoteMapper noteMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        this.noteTagRepository = noteTagRepository;
        this.noteMapper = noteMapper;
    }


    public TagDto save(TagDto tagDto) {
        Tag tag = tagMapper.toEntity(tagDto);
        return tagMapper.toDto(tagRepository.save(tag));
    }

    public TagDto getById(Long id) {
        return tagMapper.toDto(tagRepository.getById(id));
    }

    public TagDto update(TagDto tagDto, Long id) {
        Tag tag = tagMapper.toEntity(tagDto);
        return tagMapper.toDto(tagRepository.update(tag, id));
    }

    public List<TagDto> getAll() {
        return tagMapper.toDto(tagRepository.getAll());
    }

    public Map<String, Boolean> delete(Long id) {
        return tagRepository.delete(id);
    }

    public NoteDto addTagToNote(Long noteId, Long tagId) {
        return noteMapper.toDto(noteTagRepository.addTagToNote(noteId, tagId));
    }

    public NoteDto updateTagInNote(Long noteId, Long oldTagId, Long newTagId) {
        return noteMapper.toDto(noteTagRepository.updateTagInNote(noteId, oldTagId, newTagId));
    }

    public Map<String, Boolean> deleteTagFromNote(Long noteId, Long tagId) {
        return noteTagRepository.deleteTagFromNote(noteId, tagId);
    }
}
