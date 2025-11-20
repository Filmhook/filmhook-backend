package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class HomeController {

    public static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    @Operation(summary = "Say Hello", description = "Returns a simple greeting message")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("FilmHook API is running");
    }

}
