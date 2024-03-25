package com.annular.filmhook.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.DetailRequest;
import com.annular.filmhook.webmodel.IndustryTemporaryWebModel;
import com.annular.filmhook.webmodel.IndustryUserPermanentDetailWebModel;

public interface DetailService {

	ResponseEntity<?> getDetails(DetailRequest detailRequest);

	ResponseEntity<?> addTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel);

	ResponseEntity<?> getTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel);

	ResponseEntity<?> addIndustryUserPermanentDetails(
			List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels);

}
