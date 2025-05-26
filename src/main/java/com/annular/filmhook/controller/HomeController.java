package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HomeController {

    public static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/printName")
    public String testEndPoint(@RequestParam("name") String name) {
        logger.info("Hai {}", name);
        return "Hai " + name;
    }
}
