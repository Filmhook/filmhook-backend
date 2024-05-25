package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.AuditionAcceptanceWebModel;
import com.annular.filmhook.webmodel.AuditionDetailsWebModel;
import com.annular.filmhook.webmodel.AuditionIgnoranceWebModel;
import com.annular.filmhook.webmodel.AuditionWebModel;

public interface AuditionService {

	ResponseEntity<?> saveAudition(AuditionWebModel auditionWebModel);

	ResponseEntity<?> getAuditionByCategory(Integer categoryId);

	ResponseEntity<?> auditionAcceptance(AuditionAcceptanceWebModel acceptanceWebModel);

	ResponseEntity<?> auditionIgnorance(AuditionIgnoranceWebModel auditionIgnoranceWebModel);

	ResponseEntity<?> getAuditionDetails(AuditionDetailsWebModel auditionDetailsWebModel);

}
