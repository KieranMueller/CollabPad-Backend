package com.kieran.notepad.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDetails {

    private String username;
    private String password;
    private String token;
}
