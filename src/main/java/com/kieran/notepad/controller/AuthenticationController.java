package com.kieran.notepad.controller;

import com.kieran.notepad.entity.User;
import com.kieran.notepad.model.*;
import com.kieran.notepad.service.AuthService;
import com.kieran.notepad.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User request) {
        log.info("Register request received: {}", request);
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        log.info("Login request received: {}", request.getUsername());
        return authService.authenticate(request);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> hasValidToken(@RequestBody ValidateTokenRequest request) {
        log.info("Validate token request received: {}", request.getUsername());
        return ResponseEntity.ok(jwtService.isValid(request.getToken(), request.getUsername()));
    }

    @PostMapping("/username-exists")
    public ResponseEntity<Boolean> usernameExists(@RequestBody String username) {
        log.info("Received request to check if username {} exists", username);
        return ResponseEntity.ok().body(authService.usernameExists(username));
    }

    @GetMapping("/verify-email/{emailId}")
    public ResponseEntity<EmailVerificationResponse> verifyEmail(@PathVariable String emailId) {
        return authService.verifyEmail(emailId);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Boolean> sendEmailToChangePassword(@RequestBody String usernameOrEmail) {
        return authService.sendEmailToChangePassword(usernameOrEmail);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Boolean> resetPassword(@RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }

    @GetMapping("/emailId-exists/{emailId}")
    public ResponseEntity<UserResponse> emailIdExists(@PathVariable String emailId) {
        return authService.emailIdExists(emailId);
    }
}
