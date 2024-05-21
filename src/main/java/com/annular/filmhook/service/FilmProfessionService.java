package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.FilmWebModel;

public interface FilmProfessionService {

	public ResponseEntity<?> getProfessionList(FilmWebModel filmWebModel);

	public ResponseEntity<?> getProfessionMapList(FilmWebModel filmWebModel);
	

}
