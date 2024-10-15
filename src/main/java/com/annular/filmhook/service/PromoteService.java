package com.annular.filmhook.service;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.PromoteWebModel;

public interface PromoteService {

    ResponseEntity<?> updatePromote(PromoteWebModel promoteWebModel);

    ResponseEntity<?> addPromote(PromoteWebModel promoteWebModel);

    ResponseEntity<?> deletePromote(PromoteWebModel promoteWebModel);

    ResponseEntity<?> getAllPromote(PromoteWebModel promoteWebModel);

    ResponseEntity<?> getByPromoteId(PromoteWebModel promoteWebModel);

    ResponseEntity<?> deletePromoteByUserId(PromoteWebModel promoteWebModel);

	ResponseEntity<HashMap<String, Object>> addPromotes(PromoteWebModel promoteWebModel);

	ResponseEntity<?> addVisitPage(PromoteWebModel promoteWebModel);

	ResponseEntity<?> getVisitType();

	ResponseEntity<?> selectPromoteOption(PromoteWebModel promoteWebModel);

}
