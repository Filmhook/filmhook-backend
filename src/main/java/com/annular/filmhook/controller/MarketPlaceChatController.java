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
import com.annular.filmhook.service.MarketPlaceChatService;
import com.annular.filmhook.webmodel.LiveSubscribeWebModel;
import com.annular.filmhook.webmodel.MarketPlaceChatWebModel;

@RestController
@RequestMapping("/marketPlaceChat")
public class MarketPlaceChatController {
	
	
	@Autowired
	MarketPlaceChatService marketPlaceChatService;
	
	public static final Logger logger = LoggerFactory.getLogger(MarketPlaceChatController.class);
	
	@PostMapping("/saveMarketPlaceChat")
    public ResponseEntity<?> saveMarketPlaceChat(@RequestBody MarketPlaceChatWebModel marketPlaceChatWebModel) {
        try {
            logger.info("saveMarketPlaceChat controller start");
            return marketPlaceChatService.saveMarketPlaceChat(marketPlaceChatWebModel);
        } catch (Exception e) {
            logger.error("saveMarketPlaceChat Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

	@PostMapping("/updateMarketPlaceChat")
    public ResponseEntity<?> updateMarketPlaceChat(@RequestBody MarketPlaceChatWebModel marketPlaceChatWebModel) {
        try {
            logger.info("updateMarketPlaceChat controller start");
            return marketPlaceChatService.updateMarketPlaceChat(marketPlaceChatWebModel);
        } catch (Exception e) {
            logger.error("updateMarketPlaceChat Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

	

}
