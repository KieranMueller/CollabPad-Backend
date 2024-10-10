package com.kieran.notepad.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SharedNoteRequest {

    private String noteName;
    private String text;
    private String ownerUsername;
    private List<String> collaboratorUsernames;
    private String usernameMakingChange;
}
