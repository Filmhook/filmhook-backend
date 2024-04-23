package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.repository.FilmProfessionRepository;
import com.annular.filmhook.service.FilmProfessionService;
import com.annular.filmhook.webmodel.FilmWebModel;

@Service
public class FilmProfessionServiceImpl implements FilmProfessionService {

	@Autowired
	FilmProfessionRepository filmProfessionRepository;

	@Override
	public ResponseEntity<?> getProfessionList(FilmWebModel filmWebModel) {

		Optional<FilmProfession> optionalFilm = filmProfessionRepository.findById(filmWebModel.getFilmProfesssionId());
		if (optionalFilm.isPresent()) {
			FilmProfession filmProfession = optionalFilm.get();
			// Load associated sub-professions here if needed
			return ResponseEntity.ok(filmProfession);
		} else {
			// FilmProfession entity not found for the given filmProfessionId
			return ResponseEntity.notFound().build();
		}
	}

	public ResponseEntity<?> getProfessionMapList(FilmWebModel filmWebModel) {
		try {

			List<FilmProfession> professions = filmProfessionRepository.findAll();
			List<Map<String, Object>> professionList = new ArrayList<>();
			for (FilmProfession profession : professions) {
				Map<String, Object> professionMap = new HashMap<>();
				professionMap.put("filmProfessionId", profession.getFilmProfesssionId());
				professionMap.put("professionName", profession.getProfessionName());
				//professionMap.put("professionImage", Base64.getEncoder().encode(profession.getImage()));
				professionList.add(professionMap);
			}

		    Map<String, Object> response = new HashMap<>();
            response.put("professionMapList", professionList);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch professions");
		}
	}

}