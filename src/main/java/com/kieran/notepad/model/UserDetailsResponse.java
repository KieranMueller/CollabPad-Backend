package com.kieran.notepad.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kieran.notepad.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String base64Image;
    private String lastInitial;
    // new stuff
    private String description;
    private String city;
    private String state;
}
