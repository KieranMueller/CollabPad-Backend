package com.kieran.notepad.controller;

import com.kieran.notepad.model.SaveStateRequest;
import com.kieran.notepad.model.UserDetailsResponse;
import com.kieran.notepad.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class UserController {

    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveUserState(@RequestBody SaveStateRequest req) {
        log.info("Received request to save user state: {}", req);
        return userService.saveUserState(req);
    }

    @GetMapping("/state")
    public ResponseEntity<Map<String, String>> getUserState(@RequestParam String username) {
        log.info("Received request to get user state: {}", username);
        return userService.getUserState(username);
    }

    @GetMapping("/search")
    public List<UserDetailsResponse> getUsersByUsernameStartsWith(@RequestParam("user") String username) {
        log.info("Received request to get username. Searching all usernames starting with {}", username);
        return userService.getUsersByUsernameStartsWith(username);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserDetailsResponse> getUserById(@PathVariable Long id) {
        log.info("Received request to getUserById: {}", id);
        return userService.getUserById(id);
    }

    @GetMapping("user")
    public ResponseEntity<UserDetailsResponse> getUserByUsername(@RequestParam("user") String username) {
        log.info("Received request to getUserByUsername: {}", username);
        return userService.getUserByUsername(username);
    }
}
