package com.annular.filmhook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.FilmProfessionService;
import com.annular.filmhook.webmodel.FilmWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;

@RestController
@RequestMapping("/Film")
public class FilmProfessionController {
	
	@Autowired
	FilmProfessionService  filmProfessionService;
	
	@PostMapping("/getProfessionList")
	public ResponseEntity<?> getProfessionList(@RequestBody FilmWebModel filmWebModel) {
		try {
			//logger.info("getProfessionList controller start");
			return filmProfessionService.getProfessionList(filmWebModel);
		} catch (Exception e) {
			//logger.error("getProfessionList Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
}
