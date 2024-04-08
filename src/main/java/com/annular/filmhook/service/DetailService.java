package com.annular.filmhook.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.webmodel.DetailRequest;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;
import com.annular.filmhook.webmodel.IndustryTemporaryWebModel;
import com.annular.filmhook.webmodel.IndustryUserPermanentDetailWebModel;

public interface DetailService {

	ResponseEntity<?> getDetails(DetailRequest detailRequest);

	ResponseEntity<?> addTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel);

	ResponseEntity<?> getTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel);

	ResponseEntity<?> addIndustryUserPermanentDetails(
			List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels);

	FileOutputWebModel saveIndustryUserFiles(IndustryFileInputWebModel inputFileData);

	ResponseEntity<?> updateTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel);

	ResponseEntity<?> getTemporaryDuplicateDetails(IndustryTemporaryWebModel industryTemporaryWebModel);

}
