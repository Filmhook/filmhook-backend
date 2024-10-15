package com.annular.filmhook.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.repository.PostsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.Promote;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.VisitPage;
import com.annular.filmhook.repository.PromoteRepository;
import com.annular.filmhook.repository.VisitPageRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.PromoteService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.PromoteWebModel;
import com.annular.filmhook.util.Utility;

@Service
public class PromoteServiceImpl implements PromoteService {

    private static final Logger logger = LoggerFactory.getLogger(PromoteServiceImpl.class);

    @Autowired
    PromoteRepository promoteRepository;

    @Autowired
    UserDetails userDetails;
    
    @Autowired
    PostsRepository postRepository;
    
    @Autowired
    VisitPageRepository  visitPageRepository;

    @Autowired
    PostsRepository postsRepository;
    
    @Autowired
    UserService userService;
    
    @Autowired
    MediaFilesService mediaFilesService;

    @Override
    public ResponseEntity<?> addPromote(PromoteWebModel promoteWebModel) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("updatePromote method start");

            // Retrieve the existing promotion using promoteId
            Optional<Promote> optionalPromote = promoteRepository.findByPromoteId(promoteWebModel.getPromoteId());
            if (!optionalPromote.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(-1, "Promotion not found", null));
            }

            Promote promote = optionalPromote.get();
            Integer userId = userDetails.userInfo().getId();

            // Update fields with values from the request
            promote.setAmount(promoteWebModel.getAmount());
            promote.setCgst(promoteWebModel.getCgst());
            promote.setPrice(promoteWebModel.getPrice());
            promote.setNumberOfDays(promoteWebModel.getNumberOfDays());
            promote.setTotalCost(promoteWebModel.getTotalCost());
            promote.setTaxFee(promoteWebModel.getTaxFee());
            promote.setSgst(promoteWebModel.getSgst());
            promote.setStatus(promoteWebModel.getStatus()); // Set the status from the request
            promote.setUpdatedOn(new Date());
            promote.setUpdatedBy(userId);
            promote.setMultimediaId(promoteWebModel.getMultimediaId());

            if (!Utility.isNullOrEmptyList(promoteWebModel.getCountry())) {
                promote.setCountry(String.join(",", promoteWebModel.getCountry()));
            }

            // Save the updated promotion back to the repository
            promoteRepository.save(promote);
            response.put("promoteInfo", promote);

            // Updating the promote flag in the post-table
            Posts promotedPost = postsRepository.findById(promote.getPostId()).orElse(null);
            if (promotedPost != null) {
                promotedPost.setPromoteFlag(true);
                promotedPost.setPromoteStatus(true); // Assuming there's a promoteStatus field
                postsRepository.save(promotedPost);
            }

            return ResponseEntity.ok(new Response(1, "Update promote successfully", response));
        } catch (Exception e) {
            logger.error("Error updating Promote: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new Response(-1, "Error updating promote", e.getMessage()));
        }
    }


    @Override
    public ResponseEntity<?> updatePromote(PromoteWebModel promoteWebModel) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("updatePromote method start");

            // Check if promoteId is provided
            Integer promoteId = promoteWebModel.getPromoteId();
            if (promoteId == null) {
                return ResponseEntity.badRequest().body(new Response(-1, "Promote ID is required", null));
            }

            // Check if the promote with the provided promoteId exists
            Optional<Promote> data = promoteRepository.findById(promoteId);
            if (data.isPresent()) {
                Promote promote = data.get();
                promote.setAmount(promoteWebModel.getAmount());
                promote.setCgst(promoteWebModel.getCgst());
//				promote.setEndDate(promoteWebModel.getEndDate());
                promote.setPrice(promoteWebModel.getPrice());
                promote.setTotalCost(promoteWebModel.getTotalCost());
                promote.setTaxFee(promoteWebModel.getTaxFee());
                promote.setNumberOfDays(promoteWebModel.getNumberOfDays());
                promote.setSgst(promoteWebModel.getSgst());
//              promote.setCountry(promoteWebModel.getCountry());
                if (promoteWebModel.getCountry() != null) {
                    promote.setCountry(String.join(",", promoteWebModel.getCountry()));
                }
                promote.setUpdatedBy(userDetails.userInfo().getId());
                promote.setUserId(userDetails.userInfo().getId());
                promote.setMultimediaId(promoteWebModel.getMultimediaId());

                promote = promoteRepository.save(promote);
                response.put("promoteInfo", promote);
            } else {
                return ResponseEntity.ok().body(new Response(-1, "Promote with ID " + promoteId + " not found", null));
            }
            logger.info("updatePromote method end");
        } catch (Exception e) {
            logger.error("Error updating promote: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to update promote", e.getMessage()));
        }
        return ResponseEntity.ok(new Response(1, "Promote updated successfully", response));
    }

    @Override
    public ResponseEntity<?> deletePromote(PromoteWebModel promoteWebModel) {
        try {
            logger.info("deletePromote method start");
            Optional<Promote> promoteData = promoteRepository.findById(promoteWebModel.getPromoteId());
            if (promoteData.isPresent()) {
                Promote data = promoteData.get();
                data.setStatus(false);
                promoteRepository.save(data); // No need to assign the result back to data variable
                logger.info("deletePromote method end");
                return ResponseEntity.ok(new Response(1, "Success", "Promote deleted")); // Return success response
            } else {
                logger.warn("Promote data not found");
                return ResponseEntity.badRequest().body(new Response(1, "Promote data not exist", ""));
            }
        } catch (Exception e) {
            logger.error("Error deleting promote: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Delete promote failed", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getAllPromote(PromoteWebModel promoteWebModel) {
        try {
            Integer userId = userDetails.userInfo().getId();
            List<Promote> userPromotes = promoteRepository.findByUserId(userId);
            // Constructing response
            HashMap<String, Object> response = new HashMap<>();
            response.put("promotes", userPromotes);
            logger.info("getAllPromote method end");
            return ResponseEntity.ok(new Response(1, "Success", response));
        } catch (Exception e) {
            logger.error("Error in getAllPromote method: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to fetch promotes", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getByPromoteId(PromoteWebModel promoteWebModel) {
        try {
            // Get the promote ID from the PromoteWebModel
            Integer promoteId = promoteWebModel.getPromoteId();

            // Check if the promoteId is valid
            if (promoteId == null) {
                return ResponseEntity.badRequest().body(new Response(-1, "Promote ID is required", null));
            }

            // Fetch the promote from the database using its ID
            Optional<Promote> promoteOptional = promoteRepository.findById(promoteId);

            // Check if the promote exists
            if (promoteOptional.isPresent()) {
                // Construct the response with the found promote
                HashMap<String, Object> response = new HashMap<>();
                response.put("promote", promoteOptional.get());
                logger.info("getByPromoteId method end");
                return ResponseEntity.ok(new Response(1, "Success", response));
            } else {
                // If the promote doesn't exist, return a not found response
                return ResponseEntity.ok().body(new Response(-1, "Promote not found", null));
            }
        } catch (Exception e) {
            logger.error("Error in getByPromoteId method: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to fetch promote", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> deletePromoteByUserId(PromoteWebModel promoteWebModel) {
        Optional<Posts> postData = postsRepository.findById(promoteWebModel.getPostId());
        if (postData.isPresent()) {
            Posts post = postData.get();

            // Update the promotions list
            post.setPromoteStatus(false); // Set the promote status as needed

            postsRepository.save(post); // Save the updated post
            return ResponseEntity.ok("Promotion deleted successfully");
        } else {
            return ResponseEntity.ok().body("Promotion not found");
        }
    }

    @Override
    public ResponseEntity<HashMap<String, Object>> addPromotes(PromoteWebModel promoteWebModel) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("addPromote method start");

            // Retrieve user details
            Integer userId = userDetails.userInfo().getId();
            User userFromDB = userService.getUser(promoteWebModel.getUserId()).orElse(null);

            if (userFromDB != null) {
                // Build and save the Post entity
                Posts posts = Posts.builder()
                        .postId(UUID.randomUUID().toString()) // Unique post ID
                        .user(userFromDB)
                        .status(false) // Default status to false
                        .likesCount(0)
                        .promoteFlag(true) // Set promoteFlag to true for promotion
                        .promoteStatus(true) // Set promoteStatus to true for a new promotion
                        .createdOn(new Date())
                        .sharesCount(0)
                        .commentsCount(0) // Initialize comments count
                        .build();

                // Save the post to the database
                Posts savedPost = postsRepository.saveAndFlush(posts);

                // If the PromoteWebModel contains files, save them in the media_files table
                if (!Utility.isNullOrEmptyList(promoteWebModel.getFiles())) {
                    FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
                            .userId(promoteWebModel.getUserId()) // Set the user ID
                            .category(MediaFileCategory.Post) // Post category
                            .categoryRefId(savedPost.getId()) // Link media to the saved post
                            .files(promoteWebModel.getFiles()) // File list from PromoteWebModel
                            .build();

                    // Save media files in the database
                    mediaFilesService.saveMediaFiles(fileInputWebModel, userFromDB);
                }

                // Create a response HashMap with only postId, id, and userId
                response.put("postId", savedPost.getPostId());
                response.put("id", savedPost.getId());
                response.put("userId", savedPost.getUser().getUserId());

                // Return the response HashMap
                return ResponseEntity.ok(response);
            } else {
                // Handle user not found scenario by returning a bad request
                response.put("error", "User not found");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            // Log the error for debugging
            logger.error("Error in addPromote method: ", e);
            response.put("error", "Failed to add promote due to server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> addVisitPage(PromoteWebModel promoteWebModel) {
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> responseData = new HashMap<>(); // Additional response data

        try {
            logger.info("addVisitPage method start");

            // Retrieve user ID from the context
            Integer userId = userDetails.userInfo().getId();
            
            // Check if a promotion already exists for the given postId
            Promote existingPromotion = promoteRepository.findByPostId(promoteWebModel.getPostId());
            
            if (existingPromotion != null) {
                // Update the existing promotion with new values from promoteWebModel
                existingPromotion.setUserId(userId);
                existingPromotion.setVisitPage(promoteWebModel.getVisitPage());
                existingPromotion.setUpdatedBy(userId); // Set the user who updated
                existingPromotion.setUpdatedOn(new Date());
                
                // Save the updated promotion
                promoteRepository.save(existingPromotion);
                
                responseData.put("promotionId", existingPromotion.getPromoteId());
                responseData.put("postId", existingPromotion.getPostId());
                responseData.put("userId", existingPromotion.getUserId());
                response.put("message", "Promotion record updated successfully");
            } else {
                // Create a new Promote entity
                Promote promote = Promote.builder()
                        .status(true) // Set to true or as needed
                        .createdBy(userId) // Set the user who created the promotion
                        .userId(userId)
                        .postId(promoteWebModel.getPostId())
                        .visitPage(promoteWebModel.getVisitPage())
                        // Set other necessary fields from promoteWebModel if needed
                        .build();

                // Save the new Promote entity to the database
                promoteRepository.save(promote);
                
                responseData.put("promotionId", promote.getPromoteId());
                responseData.put("postId", promote.getPostId());
                responseData.put("userId", promote.getUserId());
                responseData.put("visitPage", promote.getVisitPage());
                response.put("message", "New promotion record created successfully");
            }

            // Add status and response data to the final response
            response.put("status", "success");
            response.put("response", responseData); // Include detailed response data

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Log the error for debugging
            logger.error("Error in addVisitPage method: ", e);
            response.put("status", "error");
            response.put("message", "Failed to add/update promotion due to server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> getVisitType() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch all VisitPage entries
            List<VisitPage> allVisitPages = visitPageRepository.findAll();

            // Filter entries where visitType is 'website'
            List<Map<String, Object>> websiteVisitPages = allVisitPages.stream()
                    .filter(visitPage -> "website".equals(visitPage.getVisitType()))
                    .map(visitPage -> {
                        // Create a map to store visitPageId and data only
                        Map<String, Object> filteredData = new HashMap<>();
                        filteredData.put("visitPageId", visitPage.getVisitPageId());
                        filteredData.put("data", visitPage.getData());
                        return filteredData;
                    })
                    .collect(Collectors.toList());

            // Return the filtered list
            response.put("websiteVisitPages", websiteVisitPages);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Log and handle errors
            e.printStackTrace();
            response.put("error", "Failed to retrieve visit types due to server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> selectPromoteOption(PromoteWebModel request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Retrieve the existing promotion using promoteId
            Optional<Promote> optionalPromotion = promoteRepository.findById(request.getPromoteId());

            if (optionalPromotion.isPresent()) {
                Promote promotion = optionalPromotion.get();

                // Update the fields with values from the request
                promotion.setLearnMore(request.getLearnMore());
                promotion.setShopMore(request.getShopMore());
                promotion.setWatchMe(request.getWatchMe());
                promotion.setContactUs(request.getContactUs());
                promotion.setBookNow(request.getBookNow());
                promotion.setSignUp(request.getSignUp());
                promotion.setWhatsAppNumber(request.getWhatsAppNumber());
                promotion.setWebSiteLink(request.getWebSiteLink()); // Assuming this field exists in the Promote entity

                // Save the updated promotion back to the repository
                promoteRepository.save(promotion);

                // Include postId in the response
                response.put("message", "Promotion updated successfully");
                response.put("postId", promotion.getPostId()); // Add postId to the response
                response.put("promoteId", promotion.getPromoteId());
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Promotion not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error updating promotion: ", e);
            response.put("error", "Failed to update promotion due to server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> getDescriptionByPostId(PostWebModel postWebModel) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Retrieve the post using postId from the PostWebModel
            Optional<Posts> optionalPost = postRepository.findByIds(postWebModel.getId());

            if (optionalPost.isPresent()) {
                Posts post = optionalPost.get();
                String description = post.getDescription(); // Assuming 'getDescription()' method exists

                response.put("description", description); // Return the description in the response
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Post not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error retrieving description by postId: ", e);
            response.put("error", "Failed to retrieve description due to server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> updateDescriptionByPostId(PostWebModel postWebModel) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Retrieve the existing post using postId from the PostWebModel
            Optional<Posts> optionalPost = postRepository.findByIds(postWebModel.getId());

            if (optionalPost.isPresent()) {
                Posts post = optionalPost.get();
                
                // Update the description
                post.setDescription(postWebModel.getDescription()); // Assuming 'setDescription()' method exists

                // Save the updated post back to the repository
                postRepository.save(post);
                
                response.put("message", "Post description updated successfully");
                response.put("postId", post.getId()); // Include the updated postId in the response
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Post not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error updating description by postId: ", e);
            response.put("error", "Failed to update description due to server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
