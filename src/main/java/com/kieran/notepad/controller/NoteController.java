package com.kieran.notepad.controller;

import com.kieran.notepad.model.SharedNoteRequest;
import com.kieran.notepad.model.SharedNoteResponse;
import com.kieran.notepad.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/add-user-to-notes")
    public ResponseEntity<List<SharedNoteResponse>> addSingleUserByUsernameToManyNotesByNoteId(@RequestParam("user") String username,
                                                                     @RequestParam("changer") String usernameMakingChange,
                                                                     @RequestBody List<Long> noteIds) {
        log.info("Received request to add user with username {} to the following noteIds {}", username, noteIds);
        return noteService.addSingleUserByUsernameToManyNotesByNoteId(username, usernameMakingChange, noteIds);
    }

    @PutMapping("/update-note-text/{noteId}/{username}")
    public SharedNoteResponse updateNoteTextById(@PathVariable Long noteId, @PathVariable String username,
                                                 @RequestBody(required = false) String text) {
        log.info("Received request for user {} to update text for note with id {}, to {}", username, noteId, text);
        return noteService.updateNoteTextById(noteId, username, text);
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

    @GetMapping("exists")
    public Boolean usernameExistsOnNote(@RequestParam(value = "user") String username, @RequestParam(value = "id") Long noteId) {
        log.info("Received request to check if user {} exists on note id {}", username, noteId);
        return noteService.usernameExistsOnNote(username, noteId);
    }
}
