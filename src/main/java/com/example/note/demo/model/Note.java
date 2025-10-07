package com.example.note.demo.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "notes")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "name", length = 100, nullable = false)
    String name;
    @Column(name = "date_of_creation", nullable = false)
    LocalDate dateOfCreation;
    @Column(name = "date_of_update", nullable = false)
    LocalDate dateOfUpdate;
    @Column(name = "is_done", nullable = false)
    Boolean isDone;

    @OneToMany(mappedBy = "notes", cascade = CascadeType.ALL)
    List<NoteCategory> noteCategories;

    @OneToMany(mappedBy = "notes", cascade = CascadeType.ALL)
    List<NoteTag> noteTags;
}
