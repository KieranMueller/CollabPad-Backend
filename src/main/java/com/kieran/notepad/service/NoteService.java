package com.kieran.notepad.service;

import com.kieran.notepad.entity.Note;
import com.kieran.notepad.entity.User;
import com.kieran.notepad.model.SharedNoteRequest;
import com.kieran.notepad.model.SharedNoteResponse;
import com.kieran.notepad.repository.NoteRepository;
import com.kieran.notepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public SharedNoteResponse createNewSharedNote(SharedNoteRequest req, String token) {
        try {
            if (!req.getOwnerUsername().equals(jwtService.extractUsername(token))) {
                log.error("Cannot create note with owner other than user who sent request");
                return null;
            }
            User owner = userRepository.findByUsername(req.getOwnerUsername()).get();
            List<User> collaborators = new ArrayList<>();
            req.getCollaboratorUsernames().forEach(username -> {
                Optional<User> user = userRepository.findByUsername(username);
                user.ifPresent(collaborators::add);
            });
            collaborators.add(owner);
            Note noteEntity = Note.builder()
                    .ownerId(owner.getId())
                    .noteName(req.getNoteName())
                    .collaborators(collaborators)
                    .text(req.getText())
                    .createdDate(Timestamp.from(Instant.now()).toString())
                    .build();
            Note savedNote = noteRepository.save(noteEntity);
            collaborators.forEach(user -> {
                user.getNotes().add(savedNote);
                userRepository.save(user);
            });
            return mapNoteToCreateSharedNoteResponse(savedNote);
        } catch (Exception e) {
            log.warn("Exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public SharedNoteResponse getSharedNoteById(Long id) {
        Optional<Note> opNote = noteRepository.findById(id);
        if (opNote.isEmpty()) {
            return null;
        }
        Note note = opNote.get();
        return mapNoteToCreateSharedNoteResponse(note);
    }

    public List<SharedNoteResponse> getSharedNotesByUsername(String username) {
        Optional<User> opUser = userRepository.findByUsername(username);
        if (opUser.isEmpty()) {
            return null;
        }
        User user = opUser.get();
        log.info("Found all shared notes for user {} : {}", username, user.getNotes());
        List<SharedNoteResponse> res = new ArrayList<>();
        user.getNotes().forEach(note -> res.add(mapNoteToCreateSharedNoteResponse(note)));
        return res;
    }

    public SharedNoteResponse updateNoteById(Long noteId, SharedNoteRequest req) {
        Optional<Note> opNote = noteRepository.findById(noteId);
        if (opNote.isEmpty()) {
            return null;
        }
        Note updatedNote = mapSharedNoteRequestPatchToNoteEntity(req, opNote.get());
        Note savedNote = noteRepository.save(updatedNote);
        return mapNoteToCreateSharedNoteResponse(savedNote);
    }

    public SharedNoteResponse deleteNoteById(Long noteId) {
        Optional<Note> opNote = noteRepository.findById(noteId);
        if (opNote.isEmpty()) {
            return null;
        }
        Note note = opNote.get();
        Long ownerId = note.getOwnerId();
        User owner = userRepository.findById(ownerId).get();
        owner.getNotes().remove(note);
        userRepository.save(owner);
        if (note.getCollaborators() != null) {
            note.getCollaborators().forEach(user -> {
                user.getNotes().remove(note);
                userRepository.save(user);
            });
        }
        note.setCollaborators(null);
        noteRepository.delete(note);
        return mapNoteToCreateSharedNoteResponse(note);
    }

    public Object removeUserFromNote(Long noteId, String username) {
        Optional<Note> opNote = noteRepository.findById(noteId);
        if (opNote.isEmpty()) return null;
        Note note = opNote.get();
        Optional<User> opUser = userRepository.findByUsername(username);
        if (opUser.isEmpty()) return null;
        User user = opUser.get();
        if (note.getCollaborators() == null) return null;
        user.getNotes().removeIf(n -> Objects.equals(n.getId(), noteId));
        note.getCollaborators().removeIf(u -> user.getId().equals(u.getId()));
        userRepository.save(user);
        noteRepository.save(note);
        return Map.of("message", "success");
    }

    public Object removeMultipleUsersFromNoteByUsername(Long noteId, String[] usernames) {
        try {
            for (String username : usernames) {
                removeUserFromNote(noteId, username);
            }
            return Map.of("message", "success");
        } catch (Exception e) {
            log.error("Error removing multiple users from note");
            return null;
        }
    }

    private SharedNoteResponse mapNoteToCreateSharedNoteResponse(Note note) {
        Map<String, Long> collaboratorUsernamesAndIds = new HashMap<>();
        if (note.getCollaborators() != null && !note.getCollaborators().isEmpty()) {
            for (User user : note.getCollaborators()) {
                collaboratorUsernamesAndIds.put(user.getUsername(), user.getId());
            }
        }
        return SharedNoteResponse.builder()
                .id(note.getId())
                .ownerId(note.getOwnerId())
                .ownerUsername(userRepository.findById(note.getOwnerId()).get().getUsername())
                .noteName(note.getNoteName())
                .collaboraterUsernamesAndIds(collaboratorUsernamesAndIds)
                .text(note.getText())
                .createdDate(note.getCreatedDate())
                .build();
    }

    private Note mapSharedNoteRequestPatchToNoteEntity(SharedNoteRequest req, Note note) {
        // might pose issues when trying to delete things? Like save blank text state, or no collaborators
        if (req == null)
            return note;
        if (req.getNoteName() != null && !req.getNoteName().isBlank())
            note.setNoteName(req.getNoteName());
        if (req.getText() != null && !req.getText().isBlank())
            note.setText(req.getText());
        if (req.getCollaboratorUsernames() != null && !req.getCollaboratorUsernames().isEmpty()) {
            List<User> collaborators = new ArrayList<>();
            for (String username : req.getCollaboratorUsernames()) {
                Optional<User> opUser = userRepository.findByUsername(username);
                if (note.getCollaborators().contains(opUser.get())) {
                    break;
                }
                opUser.ifPresent(collaborators::add);
                if (!opUser.get().getNotes().isEmpty()) {
                    opUser.get().getNotes().add(note);
                } else {
                    opUser.get().setNotes(List.of(note));
                }
                userRepository.save(opUser.get());

            }
            if (!note.getCollaborators().isEmpty()) {
                note.getCollaborators().addAll(collaborators);
            } else {
                note.setCollaborators(collaborators);
            }
        }
        return note;
    }
}
