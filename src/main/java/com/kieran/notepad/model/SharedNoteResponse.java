package com.kieran.notepad.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SharedNoteResponse {

    private Long id;
    private Long ownerId;
    private String ownerUsername;
    private String noteName;
    private Map<String, Long> collaboraterUsernamesAndIds;
    private String text;
    private String createdDate;
    private String websocketId;
    private String usernameLastEdited;
    private String lastEdited;
}
