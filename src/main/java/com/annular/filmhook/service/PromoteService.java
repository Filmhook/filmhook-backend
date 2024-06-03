package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.PromoteWebModel;

public interface PromoteService {

	ResponseEntity<?> updatePromote(PromoteWebModel promoteWebModel);

	ResponseEntity<?> addPromote(PromoteWebModel promoteWebModel);

	ResponseEntity<?> deletePromote(PromoteWebModel promoteWebModel);

	ResponseEntity<?> getAllPromote(PromoteWebModel promoteWebModel);

	ResponseEntity<?> getByPromoteId(PromoteWebModel promoteWebModel);

	ResponseEntity<?> deletePromoteByUserId(PromoteWebModel promoteWebModel);

}
