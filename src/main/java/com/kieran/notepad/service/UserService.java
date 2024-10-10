package com.kieran.notepad.service;

import com.kieran.notepad.entity.User;
import com.kieran.notepad.model.SaveStateRequest;
import com.kieran.notepad.model.UserDetailsResponse;
import com.kieran.notepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<Map<String, String>> saveUserState(SaveStateRequest req) {
        Optional<User> opUser = userRepository.findByUsername(req.getUsername());
        if (opUser.isEmpty()) {
            return userNotFound();
        }
        User user = opUser.get();
        user.setHistory(req.getHistory());
        log.info("Saving user {} state as {}", user.getUsername(), user.getHistory());
        userRepository.save(user);
        return ResponseEntity.ok().body(Map.of("status", "Saved user state"));
    }

    public ResponseEntity<Map<String, String>> getUserState(String username) {
        Optional<User> opUser = userRepository.findByUsername(username);
        return opUser.map(user -> new ResponseEntity<>(Map.of("history", user.getHistory() == null ? "" : user.getHistory(),
                        "websocketId", opUser.get().getWebsocketId()), HttpStatus.OK))
                .orElseGet(this::userNotFound);
    }

    public List<UserDetailsResponse> getUsersByUsernameStartsWith(String username) {
        List<User> users = userRepository.findUsersByUsernameContains(username);
        users.sort((a, b) -> {
            if (a.getUsername().length() < b.getUsername().length()) {
                return -1;
            } else if (a.getUsername().length() == b.getUsername().length()) {
                return 0;
            } else {
                return 1;
            }
        });
        List<UserDetailsResponse> userDetails = new ArrayList<>();
        if (!users.isEmpty()) {
            users.forEach(user -> {
                userDetails.add(mapUserEntityToUserDetailsResponse(user));
            });
        }
        return userDetails;
    }

    public ResponseEntity<UserDetailsResponse> getUserById(Long id) {
        Optional<User> opUser = userRepository.findById(id);
        if (opUser.isEmpty() || !opUser.get().isVerifiedEmail())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(mapUserEntityToUserDetailsResponse(opUser.get()), HttpStatus.OK);
    }

    public ResponseEntity<UserDetailsResponse> getUserByUsername(String username) {
        Optional<User> opUser = userRepository.findByUsernameAndVerifiedEmailTrue(username);
        if (opUser.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(mapUserEntityToUserDetailsResponse(opUser.get()), HttpStatus.OK);
    }

    private UserDetailsResponse mapUserEntityToUserDetailsResponse(User user) {
        try {
            String base64Image = "";
            if (user.getAvatar() != null && user.getAvatar().getContent() != null) {
                base64Image = Base64.getEncoder().encodeToString(user.getAvatar().getContent());
            }

            return UserDetailsResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .lastInitial(user.getLastName().substring(0, 1))
                    .base64Image(base64Image)
                    .build();
        } catch (Exception e) {
            log.warn("Issue mapping User entity to UserDetailsResponse: {}", user);
            return null;
        }
    }

    private ResponseEntity<Map<String, String>> userNotFound() {
        return new ResponseEntity<>(Map.of("status", "User not found"), HttpStatus.NOT_FOUND);
    }
}
