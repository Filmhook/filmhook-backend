package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.LiveStreamService;
import com.annular.filmhook.webmodel.LiveDetailsWebModel;
import com.annular.filmhook.webmodel.LiveStreamCommentWebModel;


@RestController
@RequestMapping("/live")
public class LiveStreamController {
	
	public static final Logger logger = LoggerFactory.getLogger(LiveStreamController.class);
	
	@Autowired
	LiveStreamService liveStreamService;
	
	@PostMapping("/saveLiveChannelDetails")
	public ResponseEntity<?> saveLiveDetails(@RequestBody LiveDetailsWebModel liveDetailsWebModel) {
		try {
			logger.info("saveLiveDetails controller start");
			return liveStreamService.saveLiveDetails(liveDetailsWebModel);
		} catch (Exception e) {
			logger.error("saveLiveDetails Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("/getLiveDetails")
	public ResponseEntity<?> getLiveDetails() {
		try {
			logger.info("getLiveDetails controller start");
			return liveStreamService.getLiveDetails();
		} catch (Exception e) {
			logger.error("getLiveDetails Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
	
	@PostMapping("/saveLiveStreamComment")
	public ResponseEntity<?> saveLiveStreamComment(@RequestBody LiveStreamCommentWebModel liveStreamCommentWebModel) {
		try {
			logger.info("saveLiveStreamComment controller start");
			return liveStreamService.saveLiveStreamComment(liveStreamCommentWebModel);
		} catch (Exception e) {
			logger.error("saveLiveStreamComment Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
	

	@GetMapping("/getLiveCommentDetails")
	public ResponseEntity<?> getLiveCommentDetails(@RequestParam("liveChannelId") Integer liveChannelId) {
		try {
			logger.info("getLiveCommentDetails controller start");
			return liveStreamService.getLiveCommentDetails(liveChannelId);
		} catch (Exception e) {
			logger.error("getLiveCommentDetails Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
}
