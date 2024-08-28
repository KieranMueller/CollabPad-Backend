package com.kieran.notepad.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Value("${sample.greeting}")
    private String greeting;

    @GetMapping("/greeting")
    public String greeting() {
        return greeting;
    }
}
