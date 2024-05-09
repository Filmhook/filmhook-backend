package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.Audition;
import com.annular.filmhook.model.MarketPlace;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.ShootingLocation;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.MarketPlaceRepository;
import com.annular.filmhook.repository.ShootingLocationRepository;
import com.annular.filmhook.service.MarketPlaceService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.AuditionWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.MarketPlaceWebModel;
import com.annular.filmhook.webmodel.ShootingLocationWebModel;

@Service
public class MarketPlaceServiceImpl implements MarketPlaceService {

	@Autowired
	MarketPlaceRepository marketPlaceRepository;

	@Autowired
	MediaFilesService mediaFilesService;

	@Autowired
	ShootingLocationRepository shootingLocationRepository;

	@Autowired
	UserService userService;

	@Override
	public ResponseEntity<?> saveMarketPlace(MarketPlaceWebModel marketPlaceWebModel) {
		try {

			Optional<User> userFromDB = userService.getUser(marketPlaceWebModel.getUserId());

			MarketPlace marketPlace = MarketPlace.builder().companyName(marketPlaceWebModel.getCompanyName())
					.productName(marketPlaceWebModel.getProductName()).userId(marketPlaceWebModel.getUserId())
					.productDescription(marketPlaceWebModel.getProductDescription())
					.newProduct(marketPlaceWebModel.getNewProduct()).rentalOrsale(marketPlaceWebModel.getRentalOrsale())
					.count(marketPlaceWebModel.getCount()).cost(marketPlaceWebModel.getCost()).marketPlaceIsactive(true)
					.marketPlaceCreatedBy(marketPlaceWebModel.getMarketPlaceCreatedBy()).build();

			// Save the MarketPlace entity
			MarketPlace savedMarketPlace = marketPlaceRepository.save(marketPlace);

			marketPlaceWebModel.getFileInputWebModel().setCategory(MediaFileCategory.MarketPlace);
			marketPlaceWebModel.getFileInputWebModel().setCategoryRefId(marketPlace.getMarketPlaceId());
			List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService
					.saveMediaFiles(marketPlaceWebModel.getFileInputWebModel(), userFromDB.get());

			// Prepare the response
			HashMap<String, Object> response = new HashMap<>();

			response.put("marketPlace", savedMarketPlace);
			response.put("Media files", fileOutputWebModelList);

		} catch (Exception e) {

			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseEntity.ok(new Response(1, "Success", "Market details saved successfully")));

	}

	@Override
	public ResponseEntity<?> getMarketPlaceByRentalOrSale(Boolean rentalOrsale) {
		try {
			List<MarketPlace> marketPlaces = marketPlaceRepository.findByRentalOrSale(rentalOrsale);
			if (!marketPlaces.isEmpty()) {
				List<MarketPlaceWebModel> marketPlaceWebModels = new ArrayList<>();

				for (MarketPlace marketPlace : marketPlaces) {
					MarketPlaceWebModel marketPlaceWebModel = new MarketPlaceWebModel();
					marketPlaceWebModel.setCompanyName(marketPlace.getCompanyName());
					marketPlaceWebModel.setCost(marketPlace.getCost());
					marketPlaceWebModel.setCount(marketPlace.getCount());
					marketPlaceWebModel.setMarketPlaceId(marketPlace.getMarketPlaceId());
					marketPlaceWebModel.setProductDescription(marketPlace.getProductDescription());
					marketPlaceWebModel.setNewProduct(marketPlace.getNewProduct());
					marketPlaceWebModel.setProductName(marketPlace.getProductName());
					marketPlaceWebModel.setUserId(marketPlace.getUserId());
					marketPlaceWebModel.setRentalOrsale(marketPlace.getRentalOrsale());

					List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(
							MediaFileCategory.MarketPlace, marketPlace.getMarketPlaceId());
					if (fileOutputWebModelList != null && !fileOutputWebModelList.isEmpty()) {
						marketPlaceWebModel.setFileOutputWebModel(fileOutputWebModelList);
					}

					marketPlaceWebModels.add(marketPlaceWebModel);
				}
				return ResponseEntity.status(HttpStatus.OK)
						.body(ResponseEntity.ok(new Response(1, "Success", marketPlaceWebModels)));

			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to retrieve MarketPlaces", ""));
		}
	}

	@Override
	public ResponseEntity<?> saveShootingLocation(ShootingLocationWebModel shootingLocationWebModel) {
		try {

			Optional<User> userFromDB = userService.getUser(shootingLocationWebModel.getUserId());

			ShootingLocation shootingLocation = ShootingLocation.builder()
					.shootingLocationName(shootingLocationWebModel.getShootingLocationName())
					.shootingLocationDescription(shootingLocationWebModel.getShootingLocationDescription())
					.termsAndCondition(shootingLocationWebModel.getTermsAndCondition())
					.locationUrl(shootingLocationWebModel.getLocationUrl())
					.indoorOrOutdoorLocation(shootingLocationWebModel.getIndoorOrOutdoorLocation())
					.cost(shootingLocationWebModel.getCost()).hourMonthDay(shootingLocationWebModel.getHourMonthDay())
					.shootingLocationIsactive(true)
					.shootingLocationCreatedBy(shootingLocationWebModel.getShootingLocationCreatedBy()).build();

			ShootingLocation savedShootingLocation = shootingLocationRepository.save(shootingLocation);

			shootingLocationWebModel.getFileInputWebModel().setCategory(MediaFileCategory.ShootingLocation);
			shootingLocationWebModel.getFileInputWebModel().setCategoryRefId(shootingLocation.getShootingLocationId());
			List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService
					.saveMediaFiles(shootingLocationWebModel.getFileInputWebModel(), userFromDB.get());

			// Prepare the response
			HashMap<String, Object> response = new HashMap<>();

			response.put("shootingLocation", savedShootingLocation);
			response.put("Media files", fileOutputWebModelList);

		} catch (Exception e) {

			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseEntity.ok(new Response(1, "Success", "shooting location saved successfully")));
	}

	@Override
	public ResponseEntity<?> getShootingLocation() {
		try {
			List<ShootingLocation> shootingLocation = shootingLocationRepository.findAll();
			if (!shootingLocation.isEmpty()) {
				List<ShootingLocationWebModel> shootingLocationWebModel = new ArrayList<>();

				for (ShootingLocation shootingLocations : shootingLocation) {
					ShootingLocationWebModel shootingLocationWebModels = new ShootingLocationWebModel();
					shootingLocationWebModels.setShootingLocationId(shootingLocations.getShootingLocationId());
					shootingLocationWebModels.setCost(shootingLocations.getCost());
					shootingLocationWebModels.setLocationUrl(shootingLocations.getLocationUrl());
					shootingLocationWebModels.setIndoorOrOutdoorLocation(shootingLocations.getIndoorOrOutdoorLocation());
					shootingLocationWebModels.setHourMonthDay(shootingLocations.getHourMonthDay());
					shootingLocationWebModels.setShootingLocationName(shootingLocations.getShootingLocationName());
					shootingLocationWebModels.setShootingLocationUpdatedBy(shootingLocations.getShootingLocationUpdatedBy());
					shootingLocationWebModels.setShootingLocationCreatedBy(shootingLocations.getShootingLocationCreatedBy());
					shootingLocationWebModels.setUserId(shootingLocations.getUserId());
					shootingLocationWebModels.setShootingLocationDescription(shootingLocations.getShootingLocationDescription());

					List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(
							MediaFileCategory.ShootingLocation, shootingLocations.getShootingLocationId());
					if (fileOutputWebModelList != null && !fileOutputWebModelList.isEmpty()) {
						shootingLocationWebModels.setFileOutputWebModel(fileOutputWebModelList);
					}

					shootingLocationWebModel.add(shootingLocationWebModels);
				}
				return ResponseEntity.status(HttpStatus.OK)
						.body(ResponseEntity.ok(new Response(1, "Success", shootingLocationWebModel)));

			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to retrieve MarketPlaces", ""));
		}
	}
}