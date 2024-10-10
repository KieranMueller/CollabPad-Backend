package com.kieran.notepad.service;

import com.kieran.notepad.entity.Image;
import com.kieran.notepad.entity.User;
import com.kieran.notepad.repository.ImageRepository;
import com.kieran.notepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;


    public Long uploadImage(MultipartFile multipartImage) throws IOException {
        Image image = new Image();
        image.setName(multipartImage.getName());
        image.setContent(multipartImage.getBytes());

        return imageRepository.save(image).getId();
    }

    public Resource downloadImageById(Long id) {
        byte[] image = imageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getContent();
        return new ByteArrayResource(image);
    }

    public ResponseEntity<Map<String, String>> changeProfilePicByUsername(String username, MultipartFile multipartImage) {
        try {
            if (multipartImage == null || multipartImage.isEmpty())
                return new ResponseEntity<>(Map.of("message", "invalid multipart file/image"), HttpStatus.BAD_REQUEST);
            Optional<User> opUser = userRepository.findByUsernameAndVerifiedEmailTrue(username);
            if (opUser.isEmpty())
                return new ResponseEntity<>(Map.of(
                        "message", "unable to find user with username " + username), HttpStatus.NOT_FOUND);
            User user = opUser.get();
            Image image = Image.builder()
                    .content(multipartImage.getBytes())
                    .user(user)
                    .name("profile picture")
                    .build();
            Image savedImage = imageRepository.save(image);
            user.setAvatar(savedImage);
            userRepository.save(user);
            return new ResponseEntity<>(Map.of("message", "success"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error setting profile picture for {}", username);
            return new ResponseEntity<>(Map.of("message", "something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
