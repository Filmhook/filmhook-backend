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
			logger.error("addBlock Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("/getAllBlock")
	public ResponseEntity<?> getAllBlock() {
		try {
			logger.info("getAllBlock controller start");
			return blockService.getAllBlock();
		} catch (Exception e) {
			logger.error("getAllBlock Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

}
