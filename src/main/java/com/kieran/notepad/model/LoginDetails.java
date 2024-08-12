package com.kieran.notepad.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDetails {

    private String username;
    private String password;
    private String token;
}
