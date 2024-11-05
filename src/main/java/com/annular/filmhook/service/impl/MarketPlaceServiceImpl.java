package com.annular.filmhook.service.impl;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.Date;

import com.annular.filmhook.util.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.Comment;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.MarketPlace;
import com.annular.filmhook.model.MarketPlaceChat;
import com.annular.filmhook.model.MarketPlaceLike;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.ShootingLocation;
import com.annular.filmhook.model.ShootingLocationChat;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.MarketPlaceChatRepository;
import com.annular.filmhook.repository.MarketPlaceLikeRepository;
import com.annular.filmhook.repository.MarketPlaceRepository;
import com.annular.filmhook.repository.ShootingLocationChatRepository;
import com.annular.filmhook.repository.ShootingLocationRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.MarketPlaceService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.MarketPlaceChatWebModel;
import com.annular.filmhook.webmodel.MarketPlaceLikeWebModel;
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
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    UserDetails userDetails;
    
    @Autowired
    MarketPlaceLikeRepository marketPlaceLikeRepository;
    
	@Autowired
	MarketPlaceChatRepository marketPlaceChatRepository;
	
	@Autowired
	ShootingLocationChatRepository shootingLocationChatRepository;
    
    
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
                    .conditionData(marketPlaceWebModel.getConditionData())
					.cost(marketPlaceWebModel.getCost())
					.marketPlaceIsactive(true)
                    .marketPlaceCreatedBy(marketPlaceWebModel.getMarketPlaceCreatedBy())
                    .marketPlaceCreatedOn(new Date())
                    .terms(marketPlaceWebModel.getTerms())
                    .location(marketPlaceWebModel.getLocation())
                    .url(marketPlaceWebModel.getUrl())
                    .day(marketPlaceWebModel.getDay())
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
        if (rentalOrsale == null) {
            return ResponseEntity.badRequest().body(new Response(400, "Invalid request parameters", ""));
        }
        
        List<MarketPlaceWebModel> marketPlaceWebModelList = new ArrayList<>();
        try {
            List<MarketPlace> marketPlaces = marketPlaceRepository.findByRentalOrSale(rentalOrsale);
            if (!marketPlaces.isEmpty()) {
                marketPlaceWebModelList = this.transformMarketPlaceData(marketPlaces);
            }
            return ResponseEntity.ok(new Response(1, "Success", marketPlaceWebModelList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new Response(-1, "Failed to retrieve MarketPlaces", e.getMessage()));
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
                    .placeName(shootingLocationWebModel.getPlaceName())
                    .termsAndConditions(shootingLocationWebModel.getTermsAndConditions())
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
                    shootingLocWebModel.setPlaceName(shootingLocation.getPlaceName());
                    shootingLocWebModel.setShootingLocationName(shootingLocation.getShootingLocationName());
                    shootingLocWebModel.setShootingLocationUpdatedBy(shootingLocation.getShootingLocationUpdatedBy());
                    shootingLocWebModel.setShootingLocationCreatedBy(shootingLocation.getShootingLocationCreatedBy());
                    shootingLocWebModel.setUserId(shootingLocation.getUserId());
                    shootingLocWebModel.setShootingLocationDescription(shootingLocation.getShootingLocationDescription());
                    shootingLocWebModel.setTermsAndConditions(shootingLocation.getTermsAndConditions());
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
        Map<Integer, Map<String, String>> userCache = new HashMap<>();

        try {
            if (!Utility.isNullOrEmptyList(marketPlaces)) {
                marketPlaces.stream().filter(Objects::nonNull).forEach(marketPlace -> {
                    // Retrieve media files
                    List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService
                            .getMediaFilesByCategoryAndRefId(MediaFileCategory.MarketPlace, marketPlace.getMarketPlaceId());
                    logger.info("Files retrieved for MarketPlace ID {}: {}", marketPlace.getMarketPlaceId(), fileOutputWebModelList.size());

                    // Count likes for this market place
                    Long likeCount = marketPlaceLikeRepository.countByMarketPlaceIdAndStatus(marketPlace.getMarketPlaceId());


                    
                    // Check if the logged-in user liked this market place
                    Boolean likeStatus = marketPlaceLikeRepository.existsByMarketPlaceIdAndMarketPlacelikedByAndStatus(
                            marketPlace.getMarketPlaceId(),userDetails.userInfo().getId(), true);
                    // Check if user details are cached
                    Map<String, String> userDetails = userCache.computeIfAbsent(marketPlace.getUserId(), userId -> {
                        Optional<User> userOptional = userRepository.findById(userId);
                        User user = userOptional.orElse(null);
                        Map<String, String> details = new HashMap<>();
                        if (user != null) {
                            details.put("userName", user.getName());
                            details.put("userPic", userService.getProfilePicUrl(user.getUserId()));
                            details.put("userType", user.getUserType());
                            // Set adminReview based on userType
                            if ("Industry User".equals(user.getUserType())) {
                                // Assume getAdminReview() is a method that fetches the admin review for the user
                                details.put("adminReview", String.valueOf(user.getAdminReview())); 
                            } else {
                                details.put("adminReview", null);
                            }
                        } else {
                            details.put("userName", null);
                            details.put("userPic", null);
                            details.put("userType",null);
                        }
                        return details;
                    });

                    // Build MarketPlaceWebModel
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
                            .userName(userDetails.get("userName"))   // Set userName
                            .userPic(userDetails.get("userPic"))     // Set userPic
                            .terms(marketPlace.getTerms())
                            .conditionData(marketPlace.getConditionData())
                            .location(marketPlace.getLocation())
                            .url(marketPlace.getUrl())
                            .day(marketPlace.getDay())
                            .userType(userDetails.get("userType"))
                            .adminReview(userDetails.get("adminReview"))
                            .likeCount(likeCount)
                            .likeStatus(likeStatus)
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
    @Override
    public ResponseEntity<?> getMarketPlaceByMarketTypeByUserId(MarketPlaceChatWebModel request) {
        try {
            Integer userId = userDetails.userInfo().getId();
            
            // Retrieve all chats where the user is the sender and matches the market type
            List<MarketPlaceChat> userChats = marketPlaceChatRepository.findByMarketPlaceSenderIdAndMarketType(userId, request.getMarketType());

            // Collect receiver IDs to whom the messages were sent
            Set<Integer> receiverIds = userChats.stream()
                                                 .map(MarketPlaceChat::getMarketPlaceReceiverId)
                                                 .collect(Collectors.toSet());

            // Fetch user details for the receiver IDs
            List<User> receivers = userRepository.findAllById(receiverIds);

            // Create a list to hold the unique receiver details, including accept status
            List<Map<String, Object>> uniqueReceiverData = new ArrayList<>();
            for (User receiver : receivers) {
                // Get the latest chat between the sender and this receiver with non-null accept status
                Optional<MarketPlaceChat> chatWithAcceptStatus = userChats.stream()
                    .filter(chat -> chat.getMarketPlaceReceiverId().equals(receiver.getUserId()) && chat.getAccept() != null)
                    .findFirst();

                // If a chat with an accept status is found, use that; otherwise, use the first chat found
                MarketPlaceChat chat = chatWithAcceptStatus.orElse(userChats.stream()
                    .filter(c -> c.getMarketPlaceReceiverId().equals(receiver.getUserId()))
                    .findFirst().orElse(null));
                

                if (chat != null) {
                    Map<String, Object> receiverData = new HashMap<>();
                    receiverData.put("id", receiver.getUserId());
                    receiverData.put("name", receiver.getName());
                    receiverData.put("profilePic", userService.getProfilePicUrl(receiver.getUserId()));
                    //receiverData.put("accept", (chat != null) ? chat.getAccept() : true);  // Add the accept status (true, false, or null)
                 // Determine the accept status
                    Boolean acceptStatus = (chat != null && chat.getAccept() != null) ? chat.getAccept() : true; // Default to true if chat is null or accept is null

                    receiverData.put("accept", acceptStatus);  // Add the accept status (true if no chat is found or if accept is null)

                    uniqueReceiverData.add(receiverData);
                }
            }

            // Create a response object to include the user details
            Map<String, Object> response = new HashMap<>();
            response.put("receivers", uniqueReceiverData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Log the exception (this could be with a logger or printStackTrace for debugging)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }

    }

    @Override
    public MarketPlaceLikeWebModel addMarketPlaceLike(MarketPlaceLikeWebModel marketPlaceLikeWebModel) {
        Integer marketPlaceId = marketPlaceLikeWebModel.getMarketPlaceId();
        Integer likedById = marketPlaceLikeWebModel.getMarketPlacelikedBy();
        
        MarketPlaceLike existingLike = marketPlaceLikeRepository.findByMarketPlaceIdAndMarketPlacelikedBy(marketPlaceId, likedById);
        
        if (existingLike != null) {
            // Toggle the status
            existingLike.setStatus(!existingLike.getStatus());
            existingLike.setUpdatedBy(likedById);
            existingLike.setUpdatedOn(new Date());
            
            marketPlaceLikeRepository.save(existingLike);
            return convertToWebModel(existingLike);
        } else {
            MarketPlaceLike newLike = MarketPlaceLike.builder()
                .marketPlaceId(marketPlaceId)
                .marketPlacelikedBy(likedById)
                .status(true)
                .createdBy(likedById)
                .build();
            
            marketPlaceLikeRepository.save(newLike);
            return convertToWebModel(newLike);
        }
    }

    private MarketPlaceLikeWebModel convertToWebModel(MarketPlaceLike marketPlaceLike) {
        MarketPlaceLikeWebModel webModel = new MarketPlaceLikeWebModel();
        webModel.setMarketPlaceId(marketPlaceLike.getMarketPlaceId());
        webModel.setMarketPlaceLikeId(marketPlaceLike.getMarketPlaceLikeId());
        webModel.setMarketPlacelikedBy(marketPlaceLike.getMarketPlacelikedBy());
        webModel.setStatus(marketPlaceLike.getStatus());
        webModel.setCreatedOn(marketPlaceLike.getCreatedOn());
        webModel.setUpdatedOn(marketPlaceLike.getUpdatedOn());
        // Set other fields if necessary
        return webModel;
    }

	@Override
	public ResponseEntity<?> getShootingLocationUserId() {
		try {
            Integer userId = userDetails.userInfo().getId();
            
            // Retrieve all chats where the user is the sender and matches the market type
            List<ShootingLocationChat> userChats = shootingLocationChatRepository.findByUserIdInSenderOrReceiver(userId);

            // Collect receiver IDs to whom the messages were sent
            Set<Integer> receiverIds = userChats.stream()
                                                 .map(ShootingLocationChat::getShootingLocationReceiverId)
                                                 .collect(Collectors.toSet());

            // Fetch user details for the receiver IDs
            List<User> receivers = userRepository.findAllById(receiverIds);

            // Create a list to hold the unique receiver details, including accept status
            List<Map<String, Object>> uniqueReceiverData = new ArrayList<>();
            for (User receiver : receivers) {
                // Get the latest chat between the sender and this receiver with non-null accept status
                Optional<ShootingLocationChat> chatWithAcceptStatus = userChats.stream()
                    .filter(chat -> chat.getShootingLocationReceiverId().equals(receiver.getUserId()) && chat.getAccept() != null)
                    .findFirst();

                // If a chat with an accept status is found, use that; otherwise, use the first chat found
                ShootingLocationChat chat = chatWithAcceptStatus.orElse(userChats.stream()
                    .filter(c -> c.getShootingLocationReceiverId().equals(receiver.getUserId()))
                    .findFirst().orElse(null));
                

                if (chat != null) {
                    Map<String, Object> receiverData = new HashMap<>();
                    receiverData.put("id", receiver.getUserId());
                    receiverData.put("name", receiver.getName());
                    receiverData.put("profilePic", userService.getProfilePicUrl(receiver.getUserId()));
                    //receiverData.put("accept", (chat != null) ? chat.getAccept() : true);  // Add the accept status (true, false, or null)
                 // Determine the accept status
                    Boolean acceptStatus = (chat != null && chat.getAccept() != null) ? chat.getAccept() : true; // Default to true if chat is null or accept is null

                    receiverData.put("accept", acceptStatus);  // Add the accept status (true if no chat is found or if accept is null)

                    uniqueReceiverData.add(receiverData);
                }
            }

            // Create a response object to include the user details
            Map<String, Object> response = new HashMap<>();
            response.put("receivers", uniqueReceiverData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Log the exception (this could be with a logger or printStackTrace for debugging)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
	}
    

}