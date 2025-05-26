package com.annular.filmhook.controller;

import com.annular.filmhook.util.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.BlockService;
import com.annular.filmhook.webmodel.BlockWebModel;

@RestController
@RequestMapping("/block")
public class BlockController {
	
	@Autowired
	BlockService blockService;
	
	public static final Logger logger = LoggerFactory.getLogger(BlockController.class);
	
	@PostMapping("/addBlock")
	public ResponseEntity<?> addBlock(@RequestBody BlockWebModel blockWebModel) {
		try {
			logger.info("addBlock controller start");
			return blockService.addBlock(blockWebModel);
		} catch (Exception e) {
			logger.error("addBlock Method Exception {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("/unBlock")
	public ResponseEntity<?> unBlockProfile(@RequestBody BlockWebModel blockWebModel) {
		try {
			String response = blockService.unBlockProfile(blockWebModel);
			if (!Utility.isNullOrBlankWithTrim(response) && response.contains("successfully"))
				return ResponseEntity.ok(new Response(1, response, ""));
			else return ResponseEntity.badRequest().body("Blocked profile not found...");
		} catch (Exception e) {
			logger.error("Error at unBlockProfile method... {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.internalServerError().body("Error at unblocking the profile...");
	}

	@PostMapping("/getAllBlock")
	public ResponseEntity<?> getAllBlock(@RequestBody BlockWebModel blockWebModel) {
		try {
			logger.info("getAllBlock controller start");
			return blockService.getAllBlock(blockWebModel.getBlockedBy());
		} catch (Exception e) {
			logger.error("getAllBlock Method Exception {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

}
