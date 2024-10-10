package com.kieran.notepad.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Lob
    private byte[] content;
    @OneToOne(mappedBy = "avatar")
    @JsonIgnore
    @ToString.Exclude
    private User user;
    private String name;
}
