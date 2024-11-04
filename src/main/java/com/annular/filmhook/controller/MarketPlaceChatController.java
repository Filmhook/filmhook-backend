package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.MarketPlaceChatService;
import com.annular.filmhook.webmodel.ChatWebModel;
import com.annular.filmhook.webmodel.LiveSubscribeWebModel;
import com.annular.filmhook.webmodel.MarketPlaceChatWebModel;
import com.annular.filmhook.webmodel.ShootingLocationChatWebModel;

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

	@PostMapping("/getMessageByUserIdAndMarketType")
	public ResponseEntity<?> getMessageByUserIdAndMarketType(@RequestBody MarketPlaceChatWebModel marketPlaceChatWebModel) {
		try {
			logger.info("getMessageByUserIde controller start");
			return marketPlaceChatService.getMessageByUserIdAndMarketType(marketPlaceChatWebModel);
		} catch (Exception e) {
			logger.error("getMessageByUserIdAndMarketType Method Exception {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}


	@PostMapping("/getAllUserByMarketType")
	public ResponseEntity<?> getAllUserByMarketType(@RequestBody MarketPlaceChatWebModel marketPlaceChatWebModel) {
		try {
			logger.info("getAllUserByMarketType controller start");
			return marketPlaceChatService.getAllUserByMarketType(marketPlaceChatWebModel);
		} catch (Exception e) {
			logger.error("getAllUserByMarketType Method Exception {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
	
	@PostMapping("/saveShootingLocationChat")
    public ResponseEntity<?> saveShootingLocationChat(@RequestBody ShootingLocationChatWebModel shootingLocationChatWebModel) {
        try {
            logger.info("saveShootingLocationChat controller start");
            return marketPlaceChatService.saveShootingLocationChat(shootingLocationChatWebModel);
        } catch (Exception e) {
            logger.error("saveMarketPlaceChat Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
	
	@PostMapping("/getAllUserByShootingLocationChat")
	public ResponseEntity<?> getAllUserByShootingLocationChat(@RequestBody ShootingLocationChatWebModel shootingLocationChatWebModel) {
		try {
			logger.info("getAllUserByShootingLocationChat controller start");
			return marketPlaceChatService.getAllUserByShootingLocationChat(shootingLocationChatWebModel);
		} catch (Exception e) {
			logger.error("getAllUserByShootingLocationChat Method Exception {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
	
	@PostMapping("/getShootingLocationChatByUserId")
	public ResponseEntity<?> getShootingLocationChatByUserId(@RequestBody ShootingLocationChatWebModel shootingLocationChatWebModel) {
		try {
			logger.info("getShootingLocationChatByUserId controller start");
			return marketPlaceChatService.getShootingLocationChatByUserId(shootingLocationChatWebModel);
		} catch (Exception e) {
			logger.error("getAllUserByShootingLocationChat Method Exception {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

}
