package com.kieran.notepad.service;

import com.kieran.notepad.entity.User;
import com.kieran.notepad.model.SaveStateRequest;
import com.kieran.notepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

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

    private ResponseEntity<Map<String, String>> userNotFound() {
        return new ResponseEntity<>(Map.of("status", "User not found"), HttpStatus.NOT_FOUND);
    }
}
