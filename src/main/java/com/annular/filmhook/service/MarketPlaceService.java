package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.MarketPlaceLikeWebModel;
import com.annular.filmhook.webmodel.MarketPlaceWebModel;
import com.annular.filmhook.webmodel.ShootingLocationWebModel;

import java.util.List;

public interface MarketPlaceService {

    ResponseEntity<?> saveMarketPlace(MarketPlaceWebModel marketPlaceWebModel);

    ResponseEntity<?> getMarketPlaceByRentalOrSale(Boolean rentalOrsale);

    ResponseEntity<?> saveShootingLocation(ShootingLocationWebModel shootingLocationWebModel);

    ResponseEntity<?> getShootingLocation();

    List<MarketPlaceWebModel> getUserMarketPlaces(Integer userId);

    ResponseEntity<?> getSearchMarketPlace(String searchKey);

    ResponseEntity<?> getSearchShootingLocation(String searchKey);

	ResponseEntity<?> getMarketPlaceByMarketTypeByUserId(String marketType);

	MarketPlaceLikeWebModel addMarketPlaceLike(MarketPlaceLikeWebModel marketPlaceLikeWebModel);
}
