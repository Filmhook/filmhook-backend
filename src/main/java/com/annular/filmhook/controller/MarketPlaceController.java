package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.MarketPlaceService;
import com.annular.filmhook.webmodel.MarketPlaceWebModel;
import com.annular.filmhook.webmodel.ShootingLocationWebModel;

@RestController
@RequestMapping("/marketPlace")
public class MarketPlaceController {

	@Autowired
	MarketPlaceService marketPlaceService;

	public static final Logger logger = LoggerFactory.getLogger(MarketPlaceController.class);

	@RequestMapping(path = "/marketPlace", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> saveMarketPlace(@ModelAttribute MarketPlaceWebModel marketPlaceWebModel) {
		try {
			logger.info("marketPlace data to be saved :- " + marketPlaceWebModel);
			return marketPlaceService.saveMarketPlace(marketPlaceWebModel);
		} catch (Exception e) {
			logger.error("marketPlace Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(200, "Success", ""));
	}

	@GetMapping("/getMarketPlaceByRentalOrSale")
	public ResponseEntity<?> getMarketPlaceByRentalOrSale(@RequestParam("RentalOrSale") Boolean rentalOrsale) {
		try {

			return marketPlaceService.getMarketPlaceByRentalOrSale(rentalOrsale);
		} catch (Exception e) {
			logger.error("getMarketPlaceByRentalOrSale Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(200, "Success", ""));
	}

	@GetMapping("/getUserMarketPlaces")
	public ResponseEntity<?> getUserMarketPlaces(@RequestParam("userId") Integer userId) {
		try {
			return ResponseEntity.ok().body(marketPlaceService.getUserMarketPlaces(userId));
		} catch (Exception e) {
			logger.error("getMarketPlaceByRentalOrSale Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Error at finding user market places...");
		}
	}

	@RequestMapping(path = "/saveShootingLocation", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> saveShootingLocation(@ModelAttribute ShootingLocationWebModel shootingLocationWebModel) {
		try {
			logger.info("shootingLocation data to be saved :- " + shootingLocationWebModel);
			return marketPlaceService.saveShootingLocation(shootingLocationWebModel);
		} catch (Exception e) {
			logger.error("shootingLocation Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(200, "Success", ""));
	}
	
	@GetMapping("/getShootingLocation")
	public ResponseEntity<?> getShootingLocation() {
		try {

			return marketPlaceService.getShootingLocation();
		} catch (Exception e) {
			logger.error("getShootingLocation Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(200, "Success", ""));
	}
	
	@GetMapping("/getSearchMarketPlace")
	public ResponseEntity<?> getSearchMarketPlace(@RequestParam ("searchKey") String searchKey) {
		try {
         return marketPlaceService.getSearchMarketPlace(searchKey);
		} catch (Exception e) {
			logger.error("getSearchMarketPlace Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(200, "Success", ""));
	}
	
	@GetMapping("/getSearchShootingLocation")
	public ResponseEntity<?> getSearchShootingLocation(@RequestParam ("searchKey") String searchKey) {
		try {
         return marketPlaceService.getSearchShootingLocation(searchKey);
		} catch (Exception e) {
			logger.error("getSearchShootingLocatione Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(200, "Success", ""));
	}
}
