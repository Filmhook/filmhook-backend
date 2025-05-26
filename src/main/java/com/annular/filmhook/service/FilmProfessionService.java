package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.FilmWebModel;

public interface FilmProfessionService {

    ResponseEntity<?> getProfessionList(FilmWebModel filmWebModel);

    ResponseEntity<?> getProfessionMapList(FilmWebModel filmWebModel);

}
