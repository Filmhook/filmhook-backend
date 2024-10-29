package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.MarketPlaceChatWebModel;

public interface MarketPlaceChatService {

	ResponseEntity<?> saveMarketPlaceChat(MarketPlaceChatWebModel marketPlaceChatWebModel);

	ResponseEntity<?> updateMarketPlaceChat(MarketPlaceChatWebModel marketPlaceChatWebModel);

	
}
