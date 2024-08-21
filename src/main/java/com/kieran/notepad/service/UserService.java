package com.kieran.notepad.service;

import com.kieran.notepad.model.SaveStateRequest;
import com.kieran.notepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public ResponseEntity<Map<String, String>> saveUserState(SaveStateRequest req) {
        return ResponseEntity.ok().body(Map.of("Nice", "Sweet"));
    }
}
