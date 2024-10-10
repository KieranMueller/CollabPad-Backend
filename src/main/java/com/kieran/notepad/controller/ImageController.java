package com.kieran.notepad.controller;

import com.kieran.notepad.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public Long uploadImage(@RequestParam MultipartFile multipartImage) throws IOException {
        log.info("Received multipartImage: {}", multipartImage);
        return imageService.uploadImage(multipartImage);
    }

    @GetMapping(value = "image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    private Resource downloadImageById(@PathVariable Long id) {
        log.info("Received request to download image id: {}", id);
        return imageService.downloadImageById(id);
    }

    @PostMapping("avatar/{username}")
    public ResponseEntity<Map<String, String>> changeProfilePicByUsername(@PathVariable String username,
                                                                          @RequestParam MultipartFile multipartImage) {
        log.info("Received request to change {}'s profile picture: {}", username, multipartImage.getName());
        return imageService.changeProfilePicByUsername(username, multipartImage);
    }
}
