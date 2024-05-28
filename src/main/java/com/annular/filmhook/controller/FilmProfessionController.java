package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.FilmProfessionService;
import com.annular.filmhook.webmodel.FilmWebModel;

@RestController
@RequestMapping("/Film")
public class FilmProfessionController {

	public static final Logger logger = LoggerFactory.getLogger(FilmProfessionController.class);

    @Autowired
    FilmProfessionService filmProfessionService;

    @PostMapping("/getProfessionList")
    public ResponseEntity<?> getProfessionList(@RequestBody FilmWebModel filmWebModel) {
        try {
            return filmProfessionService.getProfessionList(filmWebModel);
        } catch (Exception e) {
            logger.error("getProfessionList Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/getProfessionMapList")
    public ResponseEntity<?> getProfessionMapList(@RequestBody FilmWebModel filmWebModel) {
        try {
            return filmProfessionService.getProfessionMapList(filmWebModel);
        } catch (Exception e) {
            logger.error("getProfessionMapList Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
}
