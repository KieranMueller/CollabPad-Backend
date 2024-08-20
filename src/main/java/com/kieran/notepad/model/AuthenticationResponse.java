package com.kieran.notepad.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AuthenticationResponse {

    private String token;
    private String message;
    private String username;
    private String websocketId;
}
