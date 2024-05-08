package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.MarketPlaceWebModel;

public interface MarketPlaceService {

	ResponseEntity<?> saveMarketPlace(MarketPlaceWebModel marketPlaceWebModel);

	ResponseEntity<?> getMarketPlaceByRentalOrSale(Boolean rentalOrsale);

}
