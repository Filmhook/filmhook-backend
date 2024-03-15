package com.annular.filmhook.controller;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.PromoteService;
import com.annular.filmhook.webmodel.PromoteWebModel;

@RestController
@RequestMapping("/promote")
public class PromoteController {

	public static final Logger logger = LoggerFactory.getLogger(PromoteController.class);

	@Autowired
	PromoteService promoteService;

	@PostMapping("/addPromote")
	public ResponseEntity<?> addPromote(@RequestBody PromoteWebModel promoteWebModel) {
		try {
			logger.info("addPromote controller start");
			return promoteService.addPromote(promoteWebModel);
		} catch (Exception e) {
			logger.error("addPromote  Method Exception {} " + e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}
	@PostMapping("/updatePromote")
	public ResponseEntity<?> updatePromote(@RequestBody PromoteWebModel promoteWebModel) {
		try {
			logger.info("updatePromote controller start");
			return promoteService.updatePromote(promoteWebModel);
		} catch (Exception e) {
			logger.error("updatePromote  Method Exception {} " + e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}
	
	@PostMapping("/deletePromote")
	public ResponseEntity<?> deletePromote(@RequestBody PromoteWebModel promoteWebModel) {
		try {
			logger.info("deletePromote controller start");
			return promoteService.deletePromote(promoteWebModel);
		} catch (Exception e) {
			logger.error("deletePromote  Method Exception {} " + e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
		
	}
	
	@PostMapping("/getAllPromote")
	public ResponseEntity<?> getAllPromote(@RequestBody PromoteWebModel promoteWebModel) {
		try {
			logger.info("getAllPromote controller start");
			return promoteService.getAllPromote(promoteWebModel);
		} catch (Exception e) {
			logger.error("getAllPromote  Method Exception {} " + e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
		
	}

	@PostMapping("/getByPromoteId")
	public ResponseEntity<?> getByPromoteId(@RequestBody PromoteWebModel promoteWebModel) {
		try {
			logger.info("getByPromoteId controller start");
			return promoteService.getByPromoteId(promoteWebModel);
		} catch (Exception e) {
			logger.error("getByPromoteId  Method Exception {} " + e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
		
	}



}
