package com.annular.filmhook.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/printName")
    public String testEndPoint(@RequestParam("name") String name) {
        System.out.println("Hai " + name);
        return "Hai " + name;
    }
}
