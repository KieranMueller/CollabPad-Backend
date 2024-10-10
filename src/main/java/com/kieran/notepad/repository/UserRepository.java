package com.kieran.notepad.repository;

import com.kieran.notepad.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndVerifiedEmailTrue(String username);
    Optional<User> findByEmailId(String emailId);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndVerifiedEmailTrue(String email);
    Optional<User> findByUsernameAndVerifiedEmailTrueOrEmailAndVerifiedEmailTrue(String username, String email);
    Optional<User> findByEmailIdAndVerifiedEmailTrue(String emailId);
    @Query("SELECT u FROM User u WHERE u.username LIKE CONCAT(:username, '%')")
    List<User> findUsersByUsernameStartsWith(@Param("username") String username);
    @Query("SELECT u FROM User u WHERE u.username LIKE CONCAT('%', :username)")
    List<User> findUsersByUsernameEndsWith(@Param("username") String username);
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<User> findUsersByUsernameContains(@Param("username") String username);
}
