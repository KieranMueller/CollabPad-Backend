package com.kieran.notepad;

import com.kieran.notepad.entity.User;
import com.kieran.notepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class BeforeAppStartup implements CommandLineRunner {

    private final UserRepository userRepository;
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

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        log.info("Pre-populated database with users: {}, {}", savedUser1, savedUser2);
    }
}
