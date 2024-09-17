package com.kieran.notepad.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "note_table")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long ownerId;
    private String noteName;
    @ManyToMany(mappedBy = "notes", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<User> collaborators;
    private String text;
    @Builder.Default
    private String createdDate = Timestamp.from(Instant.now()).toString();
}
