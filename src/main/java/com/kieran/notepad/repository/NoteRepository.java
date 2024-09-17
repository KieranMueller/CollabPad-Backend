package com.kieran.notepad.repository;

import com.kieran.notepad.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("SELECT n FROM Note n JOIN n.collaborators c WHERE c.id = :userId")
    List<Note> findNotesByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Note n")
    List<Note> getAllNotes();
}
