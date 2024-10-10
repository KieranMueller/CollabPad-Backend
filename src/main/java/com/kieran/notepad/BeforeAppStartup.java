package com.kieran.notepad;

import com.kieran.notepad.entity.Image;
import com.kieran.notepad.entity.Note;
import com.kieran.notepad.entity.User;
import com.kieran.notepad.repository.NoteRepository;
import com.kieran.notepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
    private final ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:images/lemur.jpg");
        File file = resource.getFile();
        byte[] fileContent = Files.readAllBytes(file.toPath());
        InputStream in = new ByteArrayInputStream(fileContent);

        User user1 = User.builder()
                .username("demo")
                .email("dummy@gmail.com")
                .password(encoder.encode("password!"))
                .firstName("Kieran")
                .lastName("Mueller")
                .avatar(Image.builder()
                        .content(in.readAllBytes())
                        .build())
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
                .avatar(Image.builder().build())
                .history(null)
                .role(User.Role.USER)
                .verifiedEmail(true)
                .emailId(UUID.randomUUID().toString())
                .websocketId(UUID.randomUUID().toString())
                .build();
        User user3 = User.builder()
                .username("Mr_Jasper_The_Cat")
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
        User user4 = User.builder()
                .username("OLEDJasp")
                .email("dummy4@gmail.com")
                .password(encoder.encode("password!"))
                .firstName("Jasper")
                .lastName("Cat")
                .history(null)
                .role(User.Role.USER)
                .verifiedEmail(true)
                .emailId(UUID.randomUUID().toString())
                .websocketId(UUID.randomUUID().toString())
                .build();
        User user5 = User.builder()
                .username("Jasper123")
                .email("dummy5@gmail.com")
                .password(encoder.encode("password!"))
                .firstName("Jasper")
                .lastName("TheCat")
                .history(null)
                .role(User.Role.USER)
                .verifiedEmail(true)
                .emailId(UUID.randomUUID().toString())
                .websocketId(UUID.randomUUID().toString())
                .build();
        User user6 = User.builder()
                .username("Jappmeister")
                .email("dummy6@gmail.com")
                .password(encoder.encode("password!"))
                .firstName("Tommy")
                .lastName("TheCat")
                .history(null)
                .role(User.Role.USER)
                .verifiedEmail(true)
                .emailId(UUID.randomUUID().toString())
                .websocketId(UUID.randomUUID().toString())
                .build();
        User user7 = User.builder()
                .username("Jappeccio")
                .email("dummy7@gmail.com")
                .password(encoder.encode("password!"))
                .firstName("Jappos")
                .lastName("Kitty")
                .history(null)
                .role(User.Role.USER)
                .verifiedEmail(true)
                .emailId(UUID.randomUUID().toString())
                .websocketId(UUID.randomUUID().toString())
                .build();

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);
        User savedUser4 = userRepository.save(user4);
        User savedUser5 = userRepository.save(user5);
        User savedUser6 = userRepository.save(user6);
        User savedUser7 = userRepository.save(user7);

        Note note1 = Note.builder()
                .id(null)
                .ownerId(savedUser2.getId())
                .noteName("My Shared Note!")
                .collaborators(List.of(userRepository.findById(savedUser1.getId()).get()))
                .text("This is a shared note, this should be json")
                .createdDate(Timestamp.from(Instant.now()).toString())
                .lastEdited(Timestamp.from(Instant.now()).toString())
                .usernameLastEdited(savedUser2.getUsername())
                .build();

        savedUser1.setNotes(List.of(note1));
        savedUser2.setNotes(List.of(note1));

        noteRepository.save(note1);
        userRepository.saveAll(List.of(savedUser1, savedUser2));
    }
}
