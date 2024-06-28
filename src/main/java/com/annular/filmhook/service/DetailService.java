package com.annular.filmhook.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.DetailRequest;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;
import com.annular.filmhook.webmodel.IndustryTemporaryWebModel;
import com.annular.filmhook.webmodel.IndustryUserPermanentDetailWebModel;
import com.annular.filmhook.webmodel.PlatformDetailDTO;
import com.annular.filmhook.webmodel.UserWebModel;

public interface DetailService {

    ResponseEntity<?> getDetails(DetailRequest detailRequest);

    ResponseEntity<?> addTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel);

    ResponseEntity<?> getTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel);

    ResponseEntity<?> addIndustryUserPermanentDetails(Integer userId, List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels);

    List<FileOutputWebModel> saveIndustryUserFiles(IndustryFileInputWebModel inputFileData);

    ResponseEntity<?> updateTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel);

    ResponseEntity<?> getTemporaryDuplicateDetails(IndustryTemporaryWebModel industryTemporaryWebModel);

    List<FileOutputWebModel> getIndustryFiles(Integer userId);

    Resource getIndustryFile(Integer userId, String category, String fileId);

    ResponseEntity<?> getIndustryUserPermanentDetails(Integer userId);

    //ResponseEntity<?> updateIndustryUserPermanentDetails(List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels);

    ResponseEntity<?> updateIndustryUserPermanentDetails(PlatformDetailDTO platformDetailDTO);

    ResponseEntity<?> updateIndustryUserPermanentDetails(Integer userId, List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels);

    ResponseEntity<?> verifyFilmHookCode(UserWebModel userWebModel);

    ResponseEntity<?> verifyFilmHook(UserWebModel userWebModel);

    ResponseEntity<?> getIndustryByuserId(Integer userId);

}
