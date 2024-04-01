package com.annular.filmhook.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
}