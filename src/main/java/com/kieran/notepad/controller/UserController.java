package com.kieran.notepad.controller;

import com.kieran.notepad.model.SaveStateRequest;
import com.kieran.notepad.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
