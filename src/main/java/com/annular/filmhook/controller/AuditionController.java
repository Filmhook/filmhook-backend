package com.annular.filmhook.controller;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.AuditionService;
//import com.annular.filmhook.service.impl.KafkaProducer;
import com.annular.filmhook.webmodel.AuditionWebModel;

@RestController
@RequestMapping("/audition")
public class AuditionController { 

	public static final Logger logger = LoggerFactory.getLogger(AuditionController.class);
	
	@Autowired
	AuditionService auditionService;
	
//	@Autowired
//	KafkaProducer kafkaProducer;
	
    @RequestMapping(path = "/saveAudition", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> saveAudition(@ModelAttribute AuditionWebModel auditionWebModel) {
		try {
			logger.info("Audition data to be saved :- " + auditionWebModel);
			return auditionService.saveAudition(auditionWebModel);
		} catch (Exception e) {
			logger.error("Save audition Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(200, "Success", ""));
	}
    
    @GetMapping("/getAuditionByCategory")
    public ResponseEntity<?> getAuditionByCategory(@RequestParam("auditionTitle") String auditionTitle) {
        try {
        	//logger.info("Audition category to be fetched :- " + auditionWebModel.getAuditionCategory());
			return auditionService.getAuditionByCategory(auditionTitle);
		} catch (Exception e) {
			logger.error("Save audition Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(200, "Success", ""));
    }
    
    @RequestMapping(path = "/saveAuditions", method = RequestMethod.POST)
	public ResponseEntity<?> saveAsdudition() {
		try {
			logger.info("\n\n controleer to check kafka \n\n");
			//kafkaProducer.sendNotification(">>>>>>>>>>>>>>>>>>>>>>My first notifiavtion");
			return null;
		} catch (Exception e) {
			logger.error("Save audition Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(200, "Success", ""));
	}

}
