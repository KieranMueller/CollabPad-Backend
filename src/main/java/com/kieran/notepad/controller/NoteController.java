package com.kieran.notepad.controller;

import com.kieran.notepad.model.SharedNoteRequest;
import com.kieran.notepad.model.SharedNoteResponse;
import com.kieran.notepad.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    private final NoteService noteService;

    @PostMapping("/create-shared-note")
    public SharedNoteResponse createNewSharedNote(@RequestBody SharedNoteRequest note,
                                                  @RequestHeader(name = "Authorization") String token1) {
        String token = token1.substring(7);
        log.info("Received request to create new shared note: {}", note);
        return noteService.createNewSharedNote(note, token);
    }

    @GetMapping("/shared-note/{id}")
    public SharedNoteResponse getSharedNoteById(@PathVariable Long id) {
        return noteService.getSharedNoteById(id);
    }

    @GetMapping("/shared-notes")
    public List<SharedNoteResponse> getSharedNotesByUsername(@RequestParam(value = "user") String username) {
        log.info("Received request to get all shared notes for user {}", username);
        return noteService.getSharedNotesByUsername(username);
    }

    @PatchMapping("/update-note/{noteId}")
    public SharedNoteResponse updateNoteById(@PathVariable Long noteId, @RequestBody SharedNoteRequest req) {
        log.info("Received request to update note with id {}, {}", noteId, req);
        return noteService.updateNoteById(noteId, req);
    }

    @DeleteMapping("/delete-note/{noteId}")
    public SharedNoteResponse deleteNoteById(@PathVariable Long noteId) {
        log.info("Received request to delete note with id {}", noteId);
        return noteService.deleteNoteById(noteId);
    }

    @DeleteMapping("note")
    public Object removeUserFromNote(@RequestParam(value = "id") Long noteId, @RequestParam(value = "user") String username) {
        log.info("Received request to remove user {} from note with id {}", username, noteId);
        return noteService.removeUserFromNote(noteId, username);
    }

    @PostMapping("unshare/{id}")
    public Object removeMultipleUsersFromNoteByUsername(@PathVariable(value = "id") Long noteId, @RequestBody String[] usernames) {
        log.info("Received request to remove users from note with id {} : {}", noteId, usernames);
        return noteService.removeMultipleUsersFromNoteByUsername(noteId, usernames);
    }
}
