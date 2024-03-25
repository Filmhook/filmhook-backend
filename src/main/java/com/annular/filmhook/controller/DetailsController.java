package com.annular.filmhook.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.DetailService;
import com.annular.filmhook.webmodel.DetailRequest;
import com.annular.filmhook.webmodel.IndustryTemporaryWebModel;
import com.annular.filmhook.webmodel.IndustryUserPermanentDetailWebModel;

@RestController
@RequestMapping("/industryUser")
public class DetailsController {
	
	public static final Logger logger = LoggerFactory.getLogger(DetailsController.class);
	

    @Autowired
    DetailService detailService;
    
    @PostMapping("/getDetails")
    public ResponseEntity<?> getDetails(@RequestBody DetailRequest detailRequest) {
        try {
            logger.info("getDetails controller start");
            return detailService.getDetails(detailRequest);
        } catch (Exception e) {
            logger.error("getDetails Method Exception: {}", e);
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    @PostMapping("/addTemporaryDetails")
    public ResponseEntity<?> addTemporaryDetails(@RequestBody IndustryTemporaryWebModel industryTemporaryWebModel) {
        try {
            logger.info("addTemporaryDetails controller start");
            return detailService.addTemporaryDetails(industryTemporaryWebModel);
        } catch (Exception e) {
            logger.error("industryTemporaryWebModel Method Exception: {}", e);
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/getTemporaryDetails")
    public ResponseEntity<?> getTemporaryDetails(@RequestBody IndustryTemporaryWebModel industryTemporaryWebModel) {
        try {
            logger.info("getTemporaryDetails controller start");
            return detailService.getTemporaryDetails(industryTemporaryWebModel);
        } catch (Exception e) {
            logger.error("getTemporaryWebModel Method Exception: {}", e);
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    
    @PostMapping("/addIndustryUserPermanentDetails")
    public ResponseEntity<?> addIndustryUserPermanentDetails(@RequestBody List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
        try {
            logger.info("addIndustryUserPermanentDetails controller start");
            return detailService.addIndustryUserPermanentDetails(industryUserPermanentDetailWebModels);
        } catch (Exception e) {
            logger.error("addIndustryUserPermanentDetails Method Exception: {}", e);
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

}
