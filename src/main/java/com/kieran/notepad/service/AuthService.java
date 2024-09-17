package com.kieran.notepad.service;

import com.kieran.notepad.entity.User;
import com.kieran.notepad.model.*;
import com.kieran.notepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public ResponseEntity<AuthenticationResponse> register(User request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            AuthenticationResponse res = new AuthenticationResponse(null,
                    "Username " + request.getUsername() + " already exists", null, null);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmailAndVerifiedEmailTrue(request.getEmail()).isPresent()) {
            AuthenticationResponse res = new AuthenticationResponse(null,
                    "Email " + request.getEmail() + " already exists", null, null);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
        if (request.getUsername().length() < 4) {
            AuthenticationResponse res = new AuthenticationResponse(null,
                    "Username must be at least 4 characters", null, null);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
        if (request.getPassword().length() < 6) {
            AuthenticationResponse res = new AuthenticationResponse(null,
                    "Password must be at least 6 characters", null, null);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .emailId(UUID.randomUUID().toString())
                .websocketId(UUID.randomUUID().toString())
                .build();
        emailService.sendEmail(user.getEmail(), user.getUsername() + ", please verify your email for CollabPad",
                "Hi " + user.getFirstName() + "! Authenticate here (Do NOT share this link) --> " + frontendBaseUrl + "/login/" + user.getEmailId());
        user = userRepository.save(user);
        String token = jwtService.generateToken(user);
        AuthenticationResponse res = new AuthenticationResponse(token, "Success", user.getUsername(), user.getWebsocketId());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    public ResponseEntity<AuthenticationResponse> authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsernameAndVerifiedEmailTrue(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        AuthenticationResponse res = new AuthenticationResponse(token, null, user.getUsername(), user.getWebsocketId());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    public Boolean usernameExists(String username) {
        Optional<User> opUser = userRepository.findByUsername(username);
        return opUser.isPresent();
    }

    public ResponseEntity<EmailVerificationResponse> verifyEmail(String emailId) {
        Optional<User> opUser = userRepository.findByEmailId(emailId);
        if (opUser.isPresent()) {
            User user = opUser.get();
            user.setVerifiedEmail(true);
            user.setEmailId(UUID.randomUUID().toString());
            userRepository.save(user);
            String token = jwtService.generateToken(user);
            EmailVerificationResponse response = EmailVerificationResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .build();
            return ResponseEntity.ok().body(response);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Boolean> sendEmailToChangePassword(String usernameOrEmail) {
        Optional<User> opUser = userRepository.
                findByUsernameAndVerifiedEmailTrueOrEmailAndVerifiedEmailTrue(usernameOrEmail, usernameOrEmail);
        if (opUser.isPresent()) {
            User user = opUser.get();
            emailService.sendEmail(user.getEmail(), user.getUsername() + ", reset password for CollabPad",
                    "Reset password here --> " + frontendBaseUrl + "/reset-password/" + user.getEmailId());
            return ResponseEntity.ok().body(true);
        }
        return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Boolean> resetPassword(ResetPasswordRequest request) {
        Optional<User> opUser = userRepository.findByEmailIdAndVerifiedEmailTrue(request.getEmailId());
        if (opUser.isPresent()) {
            User user = opUser.get();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setEmailId(UUID.randomUUID().toString());
            userRepository.save(user);
            return ResponseEntity.ok().body(true);
        }
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<UserResponse> emailIdExists(String emailId) {
        Optional<User> opUser = userRepository.findByEmailIdAndVerifiedEmailTrue(emailId);
        if (opUser.isPresent()) {
            User user = opUser.get();
            UserResponse res = UserResponse.builder()
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();
            return ResponseEntity.ok().body(res);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
