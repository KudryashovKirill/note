package com.example.note.demo.service;

import com.example.note.demo.dto.NoteDto;
import com.example.note.demo.dto.TagDto;
import com.example.note.demo.model.Note;
import com.example.note.demo.model.Tag;
import com.example.note.demo.repository.NoteTagRepository;
import com.example.note.demo.repository.TagRepository;
import com.example.note.demo.util.mapper.NoteMapper;
import com.example.note.demo.util.mapper.TagMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CacheConfig(cacheNames = "tags")
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

    @Caching(evict = {
            @CacheEvict(value = "tags", allEntries = true),
            @CacheEvict(value = "notes", allEntries = true)
    })
    public TagDto save(TagDto tagDto) {
        log.info("Service: Saving tag with name: {} and colour: {}", tagDto.getName(), tagDto.getColour());
        Tag savedTag = tagRepository.save(tagMapper.toEntity(tagDto));
        log.info("Service: Successfully saved tag with id: {}", savedTag.getId());
        return tagMapper.toDto(tagRepository.save(savedTag));
    }

    @Cacheable(key = "#id")
    public TagDto getById(Long id) {
        log.info("Service: Fetching tag by id: {}", id);
        Tag tag = tagRepository.getById(id);
        log.debug("Service: Successfully retrieved tag: {}", tag);
        return tagMapper.toDto(tag);
    }

    @Caching(evict = {
            @CacheEvict(value = "#id"),
            @CacheEvict(value = "tags", allEntries = true),
            @CacheEvict(value = "notes", allEntries = true)
    })
    public TagDto update(TagDto tagDto, Long id) {
        log.info("Service: Updating tag with id: {} to name: {}, colour: {}",
                id, tagDto.getName(), tagDto.getColour());
        Tag tag = tagMapper.toEntity(tagDto);
        return tagMapper.toDto(tagRepository.update(tag, id));
    }

    @Cacheable
    public List<TagDto> getAll() {
        log.info("Service: Fetching all tags");
        List<Tag> tags = tagRepository.getAll();
        log.info("Service: Retrieved {} tags", tags.size());
        return tagMapper.toDto(tags);
    }

    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(value = "tags", allEntries = true),
            @CacheEvict(value = "notes", allEntries = true)
    })
    public Map<String, Boolean> delete(Long id) {
        log.info("Service: Deleting tag with id: {}", id);
        Map<String, Boolean> result = tagRepository.delete(id);
        log.info("Service: Tag deletion completed for id: {}, result: {}", id, result);
        return result;
    }

    public NoteDto addTagToNote(Long noteId, Long tagId) {
        log.info("Service: Adding tag id: {} to note id: {}", tagId, noteId);
        Note note = noteTagRepository.addTagToNote(noteId, tagId);
        log.info("Service: Successfully added tag to note");
        return noteMapper.toDto(note);
    }

    public NoteDto updateTagInNote(Long noteId, Long oldTagId, Long newTagId) {
        log.info("Service: Updating tag in note: noteId={}, oldTagId={}, newTagId={}",
                noteId, newTagId, newTagId);
        Note note = noteTagRepository.updateTagInNote(noteId, oldTagId, newTagId);
        log.info("Service: Successfully updated tag in note");
        return noteMapper.toDto(note);
    }

    public Map<String, Boolean> deleteTagFromNote(Long noteId, Long tagId) {
        log.info("Service: Deleting tag id: {} from note id: {}", tagId, noteId);
        Map<String, Boolean> result = noteTagRepository.deleteTagFromNote(noteId, tagId);
        log.info("Service: Tag deletion from note completed, result: {}", result);
        return result;
    }
}
