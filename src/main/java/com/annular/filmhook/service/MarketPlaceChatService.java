package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.MarketPlaceChatWebModel;
import com.annular.filmhook.webmodel.ShootingLocationChatWebModel;

public interface MarketPlaceChatService {

	ResponseEntity<?> saveMarketPlaceChat(MarketPlaceChatWebModel marketPlaceChatWebModel);

	ResponseEntity<?> updateMarketPlaceChat(MarketPlaceChatWebModel marketPlaceChatWebModel);

	ResponseEntity<?> getMessageByUserIdAndMarketType(MarketPlaceChatWebModel marketPlaceChatWebModel);

	ResponseEntity<?> getAllUserByMarketType(MarketPlaceChatWebModel marketPlaceChatWebModel);

	ResponseEntity<?> saveShootingLocationChat(ShootingLocationChatWebModel shootingLocationChatWebModel);

	ResponseEntity<?> getAllUserByShootingLocationChat(ShootingLocationChatWebModel shootingLocationChatWebModel);

	ResponseEntity<?> getShootingLocationChatByUserId(ShootingLocationChatWebModel shootingLocationChatWebModel);

	
}
