package com.kieran.notepad;

import com.kieran.notepad.entity.Note;
import com.kieran.notepad.entity.User;
import com.kieran.notepad.repository.NoteRepository;
import com.kieran.notepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class BeforeAppStartup implements CommandLineRunner {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        User user1 = User.builder()
                .username("demo")
                .email("dummy@gmail.com")
                .password(encoder.encode("password!"))
                .firstName("Kieran")
                .lastName("Mueller")
                .history(null)
                .role(User.Role.ADMIN)
                .verifiedEmail(true)
                .emailId(UUID.randomUUID().toString())
                .websocketId(UUID.randomUUID().toString())
                .build();
        User user2 = User.builder()
                .username("Jippers")
                .email("dummy2@gmail.com")
                .password(encoder.encode("password!"))
                .firstName("Jasper")
                .lastName("Mueller")
                .history(null)
                .role(User.Role.USER)
                .verifiedEmail(true)
                .emailId(UUID.randomUUID().toString())
                .websocketId(UUID.randomUUID().toString())
                .build();
        User user3 = User.builder()
                .username("Buddy")
                .email("dummy3@gmail.com")
                .password(encoder.encode("password!"))
                .firstName("Buddy")
                .lastName("Mueller")
                .history(null)
                .role(User.Role.USER)
                .verifiedEmail(true)
                .emailId(UUID.randomUUID().toString())
                .websocketId(UUID.randomUUID().toString())
                .build();

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        Note note1 = Note.builder()
                .id(null)
                .ownerId(savedUser2.getId())
                .noteName("My Shared Note!")
                .collaborators(List.of(userRepository.findById(savedUser1.getId()).get()))
                .text("This is a shared note, this should be json")
                .createdDate(Timestamp.from(Instant.now()).toString())
                .build();

        savedUser1.setNotes(List.of(note1));
        savedUser2.setNotes(List.of(note1));

        noteRepository.save(note1);
        userRepository.saveAll(List.of(savedUser1, savedUser2));

        log.info("Pre-populated database with users: {}, {}", savedUser1, savedUser2);
    }
}
