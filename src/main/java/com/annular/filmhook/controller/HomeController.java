package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HomeController {

    public static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("FilmHook API is running");
    }
}
