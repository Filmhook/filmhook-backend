package com.annular.filmhook.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.VisitPage;
import com.annular.filmhook.model.VisitePageCategory;
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

//	ResponseEntity<?> getVisitType();

	ResponseEntity<?> selectPromoteOption(PromoteWebModel promoteWebModel);

	ResponseEntity<?> getDescriptionByPostId(PostWebModel postWebModel);

	ResponseEntity<?> updateDescriptionByPostId(PostWebModel postWebModel);

//	ResponseEntity<?> getVisitTypeByWhatsApp();

	ResponseEntity<?> updatePromoteStatus(PromoteWebModel promoteWebModel);
	
	VisitPage addVisitPage(VisitPage visitPage);
	List<VisitPage> getPagesByCategoryId(Integer categoryId);
	List<VisitePageCategory> getAllCategories();
	
	 ResponseEntity<?> getWebsiteCategories();
	 
	 ResponseEntity<?> getWhatsAppCategories();

}
