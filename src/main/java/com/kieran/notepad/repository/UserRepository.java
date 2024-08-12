package com.kieran.notepad.repository;

import com.kieran.notepad.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndVerifiedEmailTrue(String username);
    Optional<User> findByEmailId(String emailId);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndVerifiedEmailTrue(String email);
    Optional<User> findByUsernameAndVerifiedEmailTrueOrEmailAndVerifiedEmailTrue(String username, String email);
    Optional<User> findByEmailIdAndVerifiedEmailTrue(String emailId);
}
