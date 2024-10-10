package com.kieran.notepad.repository;

import com.kieran.notepad.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
