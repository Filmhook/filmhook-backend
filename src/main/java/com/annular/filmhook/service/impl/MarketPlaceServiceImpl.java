package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.Date;

import com.annular.filmhook.util.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.MarketPlace;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.ShootingLocation;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.MarketPlaceRepository;
import com.annular.filmhook.repository.ShootingLocationRepository;
import com.annular.filmhook.service.MarketPlaceService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.MarketPlaceWebModel;
import com.annular.filmhook.webmodel.ShootingLocationWebModel;

@Service
public class MarketPlaceServiceImpl implements MarketPlaceService {

    private static final Logger logger = LoggerFactory.getLogger(MarketPlaceServiceImpl.class);

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

            MarketPlace marketPlace = MarketPlace.builder()
					.companyName(marketPlaceWebModel.getCompanyName())
                    .productName(marketPlaceWebModel.getProductName())
					.userId(marketPlaceWebModel.getUserId())
                    .productDescription(marketPlaceWebModel.getProductDescription())
                    .newProduct(marketPlaceWebModel.getNewProduct())
					.rentalOrsale(marketPlaceWebModel.getRentalOrsale())
                    .count(marketPlaceWebModel.getCount())
					.cost(marketPlaceWebModel.getCost())
					.marketPlaceIsactive(true)
                    .marketPlaceCreatedBy(marketPlaceWebModel.getMarketPlaceCreatedBy())
                    .marketPlaceCreatedOn(new Date())
					.build();

            // Save the MarketPlace entity
            MarketPlace savedMarketPlace = marketPlaceRepository.save(marketPlace);

            marketPlaceWebModel.getFileInputWebModel().setCategory(MediaFileCategory.MarketPlace);
            marketPlaceWebModel.getFileInputWebModel().setCategoryRefId(marketPlace.getMarketPlaceId());
            List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.saveMediaFiles(marketPlaceWebModel.getFileInputWebModel(), userFromDB.get());

            // Prepare the response
            HashMap<String, Object> response = new HashMap<>();
            response.put("marketPlace", savedMarketPlace);
            response.put("Media files", fileOutputWebModelList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(1, "Success", "Market details saved successfully"));
    }

    @Override
    public ResponseEntity<?> getMarketPlaceByRentalOrSale(Boolean rentalOrsale) {
        List<MarketPlaceWebModel> marketPlaceWebModelList = new ArrayList<>();
        try {
            List<MarketPlace> marketPlaces = marketPlaceRepository.findByRentalOrSale(rentalOrsale);
            if (!marketPlaces.isEmpty()) {
                marketPlaceWebModelList = this.transformMarketPlaceData(marketPlaces);
            }
            return ResponseEntity.ok().body(ResponseEntity.ok(new Response(1, "Success", marketPlaceWebModelList)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to retrieve MarketPlaces", ""));
        }
    }

    @Override
    public ResponseEntity<?> saveShootingLocation(ShootingLocationWebModel shootingLocationWebModel) {
        try {

            Optional<User> userFromDB = userService.getUser(shootingLocationWebModel.getUserId());
            logger.info("Terms and condition ->  {}", shootingLocationWebModel.getShootingTermsAndCondition());

            ShootingLocation shootingLocation = ShootingLocation.builder()
                    .userId(shootingLocationWebModel.getUserId())
                    .shootingLocationName(shootingLocationWebModel.getShootingLocationName())
                    .shootingLocationDescription(shootingLocationWebModel.getShootingLocationDescription())
                    .shootingTermsAndCondition(shootingLocationWebModel.getShootingTermsAndCondition())
                    .locationUrl(shootingLocationWebModel.getLocationUrl())
                    .indoorOrOutdoorLocation(shootingLocationWebModel.getIndoorOrOutdoorLocation())
                    .cost(shootingLocationWebModel.getCost())
					.hourMonthDay(shootingLocationWebModel.getHourMonthDay())
                    .shootingLocationIsactive(true)
                    .shootingLocationCreatedBy(shootingLocationWebModel.getShootingLocationCreatedBy())
                    .shootingLocationCreatedOn(new Date())
					.build();

            ShootingLocation savedShootingLocation = shootingLocationRepository.save(shootingLocation);

            shootingLocationWebModel.getFileInputWebModel().setCategory(MediaFileCategory.ShootingLocation);
            shootingLocationWebModel.getFileInputWebModel().setCategoryRefId(shootingLocation.getShootingLocationId());
            List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.saveMediaFiles(shootingLocationWebModel.getFileInputWebModel(), userFromDB.get());

            // Prepare the response
            HashMap<String, Object> response = new HashMap<>();
            response.put("shootingLocation", savedShootingLocation);
            response.put("Media files", fileOutputWebModelList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.ok().body(ResponseEntity.ok(new Response(1, "Success", "shooting location saved successfully")));
    }

    @Override
    public ResponseEntity<?> getShootingLocation() {
        try {
            List<ShootingLocation> shootingLocations = shootingLocationRepository.findAll();
            if (!shootingLocations.isEmpty()) {
                List<ShootingLocationWebModel> shootingLocationWebModel = this.transformShootingLocationData(shootingLocations);
                return ResponseEntity.ok().body(ResponseEntity.ok(new Response(1, "Success", shootingLocationWebModel)));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to retrieve MarketPlaces", ""));
        }
    }

    public List<ShootingLocationWebModel> transformShootingLocationData(List<ShootingLocation> shootingLocations) {
        List<ShootingLocationWebModel> shootingLocationWebModel = new ArrayList<>();
        try {
            if (!shootingLocations.isEmpty()) {
                shootingLocations.forEach(shootingLocation -> {

                    ShootingLocationWebModel shootingLocWebModel = new ShootingLocationWebModel();

                    shootingLocWebModel.setShootingLocationId(shootingLocation.getShootingLocationId());
                    shootingLocWebModel.setCost(shootingLocation.getCost());
                    shootingLocWebModel.setShootingTermsAndCondition(shootingLocation.getShootingTermsAndCondition());
                    shootingLocWebModel.setLocationUrl(shootingLocation.getLocationUrl());
                    shootingLocWebModel.setIndoorOrOutdoorLocation(shootingLocation.getIndoorOrOutdoorLocation());
                    shootingLocWebModel.setHourMonthDay(shootingLocation.getHourMonthDay());
                    shootingLocWebModel.setShootingLocationName(shootingLocation.getShootingLocationName());
                    shootingLocWebModel.setShootingLocationUpdatedBy(shootingLocation.getShootingLocationUpdatedBy());
                    shootingLocWebModel.setShootingLocationCreatedBy(shootingLocation.getShootingLocationCreatedBy());
                    shootingLocWebModel.setUserId(shootingLocation.getUserId());
                    shootingLocWebModel.setShootingLocationDescription(shootingLocation.getShootingLocationDescription());

                    // Fetch user details
                    userService.getUser(shootingLocation.getUserId()).ifPresent(user -> shootingLocWebModel.setFilmHookCode(user.getFilmHookCode()));
                    userService.getUser(shootingLocation.getUserId()).ifPresent(user -> shootingLocWebModel.setName(user.getName()));
                    
                    List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.ShootingLocation, shootingLocation.getShootingLocationId());
                    if (!Utility.isNullOrEmptyList(fileOutputWebModelList)) {
                        shootingLocWebModel.setFileOutputWebModel(fileOutputWebModelList);
                    }

                    shootingLocationWebModel.add(shootingLocWebModel);
                });
            }
        } catch (Exception e) {
            logger.error("Error at transformShootingLocation() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return shootingLocationWebModel;
    }

    @Override
    public List<MarketPlaceWebModel> getUserMarketPlaces(Integer userId) {
        List<MarketPlaceWebModel> outputList = new ArrayList<>();
        try {
            List<MarketPlace> marketPlaces = marketPlaceRepository.findByUserId(userId);
            return this.transformMarketPlaceData(marketPlaces);
        } catch (Exception e) {
            logger.error("Error at getUserMarketPlaces -> {}", e.getMessage());
            e.printStackTrace();
        }
        return outputList;
    }

    private List<MarketPlaceWebModel> transformMarketPlaceData(List<MarketPlace> marketPlaces) {
        List<MarketPlaceWebModel> outputList = new ArrayList<>();
        try {
            if (!Utility.isNullOrEmptyList(marketPlaces)) {
                marketPlaces.stream().filter(Objects::nonNull).forEach(marketPlace -> {
                    List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.MarketPlace, marketPlace.getMarketPlaceId());
                    MarketPlaceWebModel marketPlaceWebModel = MarketPlaceWebModel.builder()
                            .marketPlaceId(marketPlace.getMarketPlaceId())
							.companyName(marketPlace.getCompanyName())
                            .cost(marketPlace.getCost())
							.count(marketPlace.getCount())
                            .newProduct(marketPlace.getNewProduct())
							.productName(marketPlace.getProductName())
                            .productDescription(marketPlace.getProductDescription())
                            .rentalOrsale(marketPlace.getRentalOrsale())
							.userId(marketPlace.getUserId())
                            .marketPlaceCreatedOn(marketPlace.getMarketPlaceCreatedOn())
                            .marketPlaceCreatedBy(marketPlace.getMarketPlaceCreatedBy())
                            .marketPlaceIsactive(marketPlace.isMarketPlaceIsactive())
                            .fileOutputWebModel(fileOutputWebModelList)
							.build();
                    outputList.add(marketPlaceWebModel);
                });
            }
        } catch (Exception e) {
            logger.error("Error at transformMarketPlaceData() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return outputList;
    }

    @Override
    public ResponseEntity<?> getSearchMarketPlace(String searchKey) {
        List<MarketPlaceWebModel> marketPlaceWebModelList = new ArrayList<>();
        try {
            List<MarketPlace> marketPlaces = marketPlaceRepository.findBySearchKey(searchKey);
            if (!marketPlaces.isEmpty()) {
                marketPlaceWebModelList = this.transformMarketPlaceData(marketPlaces);
            }
            return ResponseEntity.ok(new Response(1, "Success", marketPlaceWebModelList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to retrieve MarketPlaces", ""));
        }
    }

    @Override
    public ResponseEntity<?> getSearchShootingLocation(String searchKey) {
        try {
            List<ShootingLocation> shootingLocations = shootingLocationRepository.findBySearchKey(searchKey);
            if (!shootingLocations.isEmpty()) {
                List<ShootingLocationWebModel> shootingLocationWebModel = this.transformShootingLocationData(shootingLocations);
                return ResponseEntity.ok().body(ResponseEntity.ok(new Response(1, "Success", shootingLocationWebModel)));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to retrieve MarketPlaces", ""));
        }
    }

}