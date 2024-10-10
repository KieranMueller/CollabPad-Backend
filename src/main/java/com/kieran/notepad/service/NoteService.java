package com.kieran.notepad.service;

import com.kieran.notepad.entity.Note;
import com.kieran.notepad.entity.User;
import com.kieran.notepad.model.SharedNoteRequest;
import com.kieran.notepad.model.SharedNoteResponse;
import com.kieran.notepad.repository.NoteRepository;
import com.kieran.notepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Time;
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
                    .usernameLastEdited(owner.getUsername())
                    .lastEdited(Timestamp.from(Instant.now()).toString())
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

    public SharedNoteResponse updateNoteTextById(Long noteId, String username, String text) {
        Optional<Note> opNote = noteRepository.findById(noteId);
        if (opNote.isEmpty()) return null;
        Note note = opNote.get();
        Optional<User> opUser = userRepository.findByUsername(username);
        if (opUser.isEmpty()) return null;
        note.setText(text);
        note.setUsernameLastEdited(username);
        note.setLastEdited(Timestamp.from(Instant.now()).toString());
        Note savedNote = noteRepository.save(note);
        return mapNoteToCreateSharedNoteResponse(savedNote);
    }

    public Boolean usernameExistsOnNote(String username, Long noteId) {
        Optional<Note> opNote = noteRepository.findById(noteId);
        if (opNote.isEmpty()) return false;
        Note note = opNote.get();
        Optional<User> opUser = userRepository.findByUsername(username);
        if (opUser.isEmpty()) return false;
        for (User user : note.getCollaborators()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<List<SharedNoteResponse>> addSingleUserByUsernameToManyNotesByNoteId(String username, String usernameMakingChange,
                                                                                               List<Long> noteIds) {
        try {
            Optional<User> opUser = userRepository.findByUsernameAndVerifiedEmailTrue(username);
            if (opUser.isEmpty()) return null;
            User user = opUser.get();
            List<SharedNoteResponse> listToReturn = new ArrayList<>();
            List<Note> savedNotes = new ArrayList<>();
            for (Long noteId : noteIds) {
                Optional<Note> opNote = noteRepository.findById(noteId);
                if (opNote.isEmpty()) break;
                Note note = opNote.get();
                if (note.getCollaborators() != null && !note.getCollaborators().isEmpty()) {
                    note.getCollaborators().add(user);
                } else {
                    note.setCollaborators(List.of(user));
                }
                note.setLastEdited(Timestamp.from(Instant.now()).toString());
                note.setUsernameLastEdited(usernameMakingChange);
                savedNotes.add(noteRepository.save(note));
                listToReturn.add(mapNoteToCreateSharedNoteResponse(note));
            };
            if (user.getNotes() != null && !user.getNotes().isEmpty()) {
                user.getNotes().addAll(savedNotes);
            } else {
                user.setNotes(savedNotes);
            }
            userRepository.save(user);
            return new ResponseEntity<>(listToReturn, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error adding user to multiple notes {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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
                .websocketId(note.getWebsocketId())
                .usernameLastEdited(note.getUsernameLastEdited())
                .lastEdited(note.getLastEdited())
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
        note.setUsernameLastEdited(req.getUsernameMakingChange());
        note.setLastEdited(Timestamp.from(Instant.now()).toString());
        return note;
    }
}
