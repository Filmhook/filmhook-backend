package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.AuditionWebModel;

public interface AuditionService {

	ResponseEntity<?> saveAudition(AuditionWebModel auditionWebModel);

	ResponseEntity<?> getAuditionByCategory(Integer categoryId);

}
