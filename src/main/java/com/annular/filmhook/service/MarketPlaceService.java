package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.MarketPlaceWebModel;
import com.annular.filmhook.webmodel.ShootingLocationWebModel;

public interface MarketPlaceService {

	ResponseEntity<?> saveMarketPlace(MarketPlaceWebModel marketPlaceWebModel);

	ResponseEntity<?> getMarketPlaceByRentalOrSale(Boolean rentalOrsale);

	ResponseEntity<?> saveShootingLocation(ShootingLocationWebModel shootingLocationWebModel);

	ResponseEntity<?> getShootingLocation();

}
