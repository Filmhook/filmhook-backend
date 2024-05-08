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
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.MarketPlaceRepository;
import com.annular.filmhook.service.MarketPlaceService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.AuditionWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.MarketPlaceWebModel;

@Service
public class MarketPlaceServiceImpl implements MarketPlaceService {
	
	@Autowired
	MarketPlaceRepository marketPlaceRepository;
	
	@Autowired
	MediaFilesService mediaFilesService;
	
	
	@Autowired
	UserService userService;

	@Override
	public ResponseEntity<?> saveMarketPlace(MarketPlaceWebModel marketPlaceWebModel) {
		try {
  
			 Optional<User> userFromDB = userService.getUser(marketPlaceWebModel.getUserId());

			MarketPlace marketPlace = MarketPlace.builder().companyName(marketPlaceWebModel.getCompanyName())
					.productName(marketPlaceWebModel.getProductName())
					.userId(marketPlaceWebModel.getUserId())
					.productDescription(marketPlaceWebModel.getProductDescription())
					.newProduct(marketPlaceWebModel.getNewProduct()).rentalOrsale(marketPlaceWebModel.getRentalOrsale())
					.count(marketPlaceWebModel.getCount()).cost(marketPlaceWebModel.getCost())
					.marketPlaceIsactive(true)
					.marketPlaceCreatedBy(marketPlaceWebModel.getMarketPlaceCreatedBy())
                     .build();

			// Save the MarketPlace entity
			MarketPlace savedMarketPlace = marketPlaceRepository.save(marketPlace);
			
			marketPlaceWebModel.getFileInputWebModel().setCategory(MediaFileCategory.MarketPlace);
			marketPlaceWebModel.getFileInputWebModel().setCategoryRefId(marketPlace.getMarketPlaceId()); 
			List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.saveMediaFiles(marketPlaceWebModel.getFileInputWebModel(),userFromDB.get() );


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

	                List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.MarketPlace, marketPlace.getMarketPlaceId());
	                if (fileOutputWebModelList != null && !fileOutputWebModelList.isEmpty()) {
	                    marketPlaceWebModel.setFileOutputWebModel(fileOutputWebModelList);
	                }

	                marketPlaceWebModels.add(marketPlaceWebModel);
	            }

	            return ResponseEntity.ok(marketPlaceWebModels);
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
