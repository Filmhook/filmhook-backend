package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.AuditionAcceptanceWebModel;
import com.annular.filmhook.webmodel.AuditionWebModel;

public interface AuditionService {

	ResponseEntity<?> saveAudition(AuditionWebModel auditionWebModel);

	ResponseEntity<?> getAuditionByCategory(String auditionTitle);

	ResponseEntity<?> auditionAcceptance(AuditionAcceptanceWebModel acceptanceWebModel);

}
