package com.annular.filmhook.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.repository.MediaFilesRepository;
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
import com.annular.filmhook.model.VisitePageCategory;
import com.annular.filmhook.repository.PromoteRepository;
import com.annular.filmhook.repository.VisitPageCategoryRepository;
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
	MediaFilesRepository mediaFilesRepository;
	@Autowired
	UserDetails userDetails;
	
	@Autowired
	PostsRepository postRepository;
	@Autowired
    VisitPageCategoryRepository categoryRepository;

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
			promote.setStatus(true); 
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
				promotedPost.setPromoteFlag(false);
				promotedPost.setPromoteStatus(false); // Assuming there's a promoteStatus field
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

//	@Override
//	public ResponseEntity<HashMap<String, Object>> addPromotes(PromoteWebModel promoteWebModel) {
//		HashMap<String, Object> response = new HashMap<>();
//		try {
//			logger.info("addPromote method start");
//
//			// Retrieve user details
//			Integer userId = userDetails.userInfo().getId();
//			User userFromDB = userService.getUser(promoteWebModel.getUserId()).orElse(null);
//
//			if (userFromDB != null) {
//				// Build and save the Post entity
//				Posts posts = Posts.builder()
//						.postId(UUID.randomUUID().toString()) // Unique post ID
//						.user(userFromDB)
//						.status(false)
//						.likesCount(0)
//						.promoteFlag(true) // Set promoteFlag to true for promotion
//						.promoteStatus(true) // Set promoteStatus to true for a new promotion
//						.createdOn(new Date())
//						.createdBy(userId)
//						.sharesCount(0)
//						.commentsCount(0) // Initialize comments count
//						.build();
//
//				// Save the post to the database
//				Posts savedPost = postsRepository.saveAndFlush(posts);
//
//				// If the PromoteWebModel contains files, save them in the media_files table
//				if (!Utility.isNullOrEmptyList(promoteWebModel.getFiles())) {
//					FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
//							.userId(promoteWebModel.getUserId()) // Set the user ID
//							.category(MediaFileCategory.Post) // Post category
//							.categoryRefId(savedPost.getId()) // Link media to the saved post
//							.files(promoteWebModel.getFiles()) // File list from PromoteWebModel
//							.build();
//
//					// Save media files in the database
//					mediaFilesService.saveMediaFiles(fileInputWebModel, userFromDB);
//				}
//
//				// Create a response HashMap with only postId, id, and userId
//				response.put("postId", savedPost.getPostId());
//				response.put("id", savedPost.getId());
//				response.put("userId", savedPost.getUser().getUserId());
//
//				// Return the response HashMap
//				return ResponseEntity.ok(response);
//			} else {
//				// Handle user not found scenario by returning a bad request
//				response.put("error", "User not found");
//				return ResponseEntity.badRequest().body(response);
//			}
//
//		} catch (Exception e) {
//			// Log the error for debugging
//			logger.error("Error in addPromote method: ", e);
//			response.put("error", "Failed to add promote due to server error");
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//	}
	
	@Override
	public ResponseEntity<HashMap<String, Object>> addPromotes(PromoteWebModel promoteWebModel) {
		  HashMap<String, Object> response = new HashMap<>();
		    try {
		        logger.info("addPromotes method start");

		        // ✅ Check if postId is passed
		        if (promoteWebModel.getPostId() == null) {
		            response.put("error", "postId is required to upload media");
		            return ResponseEntity.badRequest().body(response);
		        }

		        Optional<Posts> optionalPost = postsRepository.findByPostId(promoteWebModel.getPostId());
		        if (optionalPost.isEmpty()) {
		            response.put("error", "Invalid postId, post not found");
		            return ResponseEntity.badRequest().body(response);
		        }

		        Posts post = optionalPost.get();
		        User userFromDB = post.getUser();

		        
				// ✅ Check if media already exists for this post
		        List<MediaFiles> existingMedia = mediaFilesRepository.findByCategoryAndCategoryRefId(
		                MediaFileCategory.Post, post.getId());

		        if (existingMedia != null && !existingMedia.isEmpty()) {
		            // 🔹 If already media present, return existing instead of adding new
		            response.put("status", "success");
		            response.put("message", "Media already exists for this post");
		            response.put("postId", post.getPostId());
		            response.put("media", existingMedia);
		            return ResponseEntity.ok(response);
		        }

		        // ✅ If no existing media, upload new ones if provided
		        if (!Utility.isNullOrEmptyList(promoteWebModel.getFiles())) {
		            FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
		                    .userId(userFromDB.getUserId())
		                    .category(MediaFileCategory.Post)
		                    .categoryRefId(post.getId())
		                    .files(promoteWebModel.getFiles())
		                    .build();

		            mediaFilesService.saveMediaFiles(fileInputWebModel, userFromDB);

		            response.put("status", "success");
		            response.put("message", "Media uploaded successfully");
		            response.put("postId", post.getPostId());
		            return ResponseEntity.ok(response);

		        } else {
		            response.put("warning", "No media files provided and no existing media found");
		            return ResponseEntity.badRequest().body(response);
		        }

		    } catch (Exception e) {
		        logger.error("Error in addPromotes method: ", e);
		        response.put("error", "Failed to upload media due to server error");
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		    }
		}
	


//	@Override
//	public ResponseEntity<?> addVisitPage(PromoteWebModel promoteWebModel) {
//		HashMap<String, Object> response = new HashMap<>();
//		HashMap<String, Object> responseData = new HashMap<>(); // Additional response data
//
//		try {
//			logger.info("addVisitPage method start");
//
//			// Retrieve user ID from the context
//			Integer userId = userDetails.userInfo().getId();
//
//			// Check if a promotion already exists for the given postId
//			Promote existingPromotion = promoteRepository.findByPostId(promoteWebModel.getPostId());
//
//			if (existingPromotion != null) {
//				// Update the existing promotion with new values from promoteWebModel
//				existingPromotion.setUserId(userId);
//				existingPromotion.setVisitPage(promoteWebModel.getVisitPage());
//				existingPromotion.setUpdatedBy(userId); // Set the user who updated
//				existingPromotion.setUpdatedOn(new Date());
//				existingPromotion.setBrandName(promoteWebModel.getBrandName());
//				existingPromotion.setCompanyType(promoteWebModel.getCompanyType());
//
//				// ✅ If companyType is NOT "Individual", update companyName
//				if (!"Individual".equalsIgnoreCase(promoteWebModel.getCompanyType())) {
//					existingPromotion.setCompanyName(promoteWebModel.getCompanyName());
//				} else {
//					existingPromotion.setCompanyName(null); 
//				}
//				//existingPromotion.setStatus(true);             
//				promoteRepository.save(existingPromotion);
//
//				responseData.put("promotionId", existingPromotion.getPromoteId());
//				responseData.put("postId", existingPromotion.getPostId());
//				responseData.put("userId", existingPromotion.getUserId());
//				responseData.put("visitPage", existingPromotion.getVisitPage());
//				response.put("message", "Promotion record updated successfully");
//			} else {
//				// Create a new Promote entity
//				Promote promote = Promote.builder()
//						//.status(true) // Set to true or as needed
//						.createdBy(userId) // Set the user who created the promotion
//						.userId(userId)
//						.postId(promoteWebModel.getPostId())
//						.visitPage(promoteWebModel.getVisitPage()) .brandName(promoteWebModel.getBrandName())
//						.companyType(promoteWebModel.getCompanyType())
//						.companyName(
//								!"Individual".equalsIgnoreCase(promoteWebModel.getCompanyType()) 
//								? promoteWebModel.getCompanyName() 
//										: null
//								)
//						// Set other necessary fields from promoteWebModel if needed
//						.build();
//
//				// Save the new Promote entity to the database
//				promoteRepository.save(promote);
//
//				responseData.put("promotionId", promote.getPromoteId());
//				responseData.put("postId", promote.getPostId());
//				responseData.put("userId", promote.getUserId());
//				responseData.put("visitPage", promote.getVisitPage());
//				response.put("message", "New promotion record created successfully");
//			}
//
//			// Add status and response data to the final response
//			response.put("status", "success");
//			response.put("response", responseData); // Include detailed response data
//
//			return ResponseEntity.ok(response);
//
//		} catch (Exception e) {
//			// Log the error for debugging
//			logger.error("Error in addVisitPage method: ", e);
//			response.put("status", "error");
//			response.put("message", "Failed to add/update promotion due to server error");
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//	}

	
	@Override
	public ResponseEntity<?> addVisitPage(PromoteWebModel promoteWebModel) {
		  Map<String, Object> response = new HashMap<>();
		    Map<String, Object> responseData = new HashMap<>();

		    try {
		        logger.info("addOrPromotePost method start");

		        Integer userId = userDetails.userInfo().getId();
		        User userFromDB = userService.getUser(promoteWebModel.getUserId()).orElse(null);

		        if (userFromDB == null) {
		            response.put("status", "error");
		            response.put("message", "User not found");
		            return ResponseEntity.badRequest().body(response);
		        }

		        Posts post;

		        // ✅ Case 1: If postId is provided, use existing post
		        if (promoteWebModel.getPostId() != null) {
		            post = postsRepository.findById(promoteWebModel.getPostId()).orElse(null);

		            if (post == null) {
		                response.put("status", "error");
		                response.put("message", "Post not found with ID: " + promoteWebModel.getPostId());
		                return ResponseEntity.badRequest().body(response);
		            }

		            // update promote flags (in case it's not marked as promoted yet)
		            post.setPromoteFlag(false);
		            
		            post.setPromoteStatus(false);
		            post.setStatus(true);
		            postsRepository.save(post);

		        } else {
		            // ✅ Case 2: No postId → create a new post
		            post = Posts.builder()
		                    .postId(UUID.randomUUID().toString())
		                    .user(userFromDB)
		                    .status(false)
		                    .likesCount(0)
		                    .promoteFlag(false)
		                    .promoteStatus(false)
		                    .createdOn(new Date())
		                    .createdBy(userId)
		                    .sharesCount(0)
		                    .commentsCount(0)
		                    .build();

		            post = postsRepository.saveAndFlush(post);
		        }

		        // ✅ Create or Update Promote
		        Promote promote = Promote.builder()
		                .postId(post.getId())
		                .userId(userId)
		                .visitPage(promoteWebModel.getVisitPage())
		                .brandName(promoteWebModel.getBrandName())
		                .companyType(promoteWebModel.getCompanyType())
		                .companyName("Individual".equalsIgnoreCase(promoteWebModel.getCompanyType())
		                        ? null
		                        : promoteWebModel.getCompanyName())
		                .nation(promoteWebModel.getNation())
		                .createdBy(userId)
		                .createdOn(new Date())
		                .status(false)
		                .build();

		        // ✅ Save company logo if provided
		        if (promoteWebModel.getCompanyLogo() != null && !promoteWebModel.getCompanyLogo().isEmpty()) {
		            FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
		                    .userId(userFromDB.getUserId())
		                    .category(MediaFileCategory.Promote)
		                    .categoryRefId(post.getId())
		                    .files(List.of(promoteWebModel.getCompanyLogo()))
		                    .build();

		            mediaFilesService.saveMediaFiles(fileInputWebModel, userFromDB);

		            promote.setCompanyLogo(promoteWebModel.getCompanyLogo().getOriginalFilename());
		        }

		        Promote savedPromotion = promoteRepository.save(promote);

		        // ✅ Response
		        responseData.put("promotionId", savedPromotion.getPromoteId());
		        responseData.put("postId", post.getId());
		        responseData.put("userId", savedPromotion.getUserId());
		        responseData.put("visitPage", savedPromotion.getVisitPage());
		        responseData.put("nation", savedPromotion.getNation());
		        responseData.put("companyLogo", savedPromotion.getCompanyLogo());

		        response.put("status", "success");
		        response.put("response", responseData);

		        return ResponseEntity.ok(response);

		    } catch (Exception e) {
		        logger.error("Error in addOrPromotePost method: ", e);
		        response.put("status", "error");
		        response.put("message", "Failed to add or promote post due to server error");
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		    }
	}


//	@Override
//	public ResponseEntity<?> getVisitType() {
//		Map<String, Object> response = new HashMap<>();
//
//		try {
//			// Fetch all VisitPage entries
//			List<VisitPage> allVisitPages = visitPageRepository.findAll();
//
//			// Filter entries where visitType is 'website'
//			List<Map<String, Object>> websiteVisitPages = allVisitPages.stream()
//					.filter(visitPage -> "website".equals(visitPage.getVisitType()))
//					.map(visitPage -> {
//						// Create a map to store visitPageId and data only
//						Map<String, Object> filteredData = new HashMap<>();
//						filteredData.put("visitPageId", visitPage.getVisitPageId());
//						filteredData.put("data", visitPage.getData());
//						return filteredData;
//					})
//					.collect(Collectors.toList());
//
//			// Return the filtered list
//			response.put("websiteVisitPages", websiteVisitPages);
//			return ResponseEntity.ok(response);
//
//		} catch (Exception e) {
//			// Log and handle errors
//			e.printStackTrace();
//			response.put("error", "Failed to retrieve visit types due to server error");
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//	}

	@Override
	public ResponseEntity<?> selectPromoteOption(PromoteWebModel request) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        // 🔹 Retrieve promotion using promoteId
	        Optional<Promote> optionalPromotion = promoteRepository.findById(request.getPromoteId());

	        if (optionalPromotion.isEmpty()) {
	            response.put("error", "Promotion not found");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }

	        Promote promotion = optionalPromotion.get();

	        // 🔹 Update promotion fields
	        promotion.setSelectOption(request.getSelectOption());
	        promotion.setContactNumber(request.getContactNumber());
	        promotion.setWebSiteLink(request.getWebSiteLink());

	        promoteRepository.save(promotion);

	        // 🔹 Update Post description if provided
	        if (request.getDescription() != null && !request.getDescription().isBlank()) {
	            postsRepository.findById(promotion.getPostId()).ifPresent(post -> {
	                post.setDescription(request.getDescription());
	                postsRepository.save(post);
	            });
	        }

	        // 🔹 Save images if provided
	        if (!Utility.isNullOrEmptyList(request.getFiles())) {
	            postsRepository.findById(promotion.getPostId()).ifPresent(post -> {
	                User userFromDB = post.getUser();

	                FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
	                        .userId(userFromDB.getUserId())
	                        .category(MediaFileCategory.Post) // Store under Post category
	                        .categoryRefId(post.getId())      // Link with postId
	                        .files(request.getFiles())        // Uploaded files
	                        .build();

	                mediaFilesService.saveMediaFiles(fileInputWebModel, userFromDB);
	                response.put("mediaStatus", "Media uploaded successfully");
	            });
	        }

	        // 🔹 Final Response
	        response.put("message", "Promotion updated successfully");
	        response.put("postId", promotion.getPostId());
	        response.put("promoteId", promotion.getPromoteId());

	        return ResponseEntity.ok(response);

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
				String description = post.getDescription(); 

				response.put("description", description); 
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


//	@Override
//	public ResponseEntity<?> getVisitTypeByWhatsApp() {
//
//		Map<String, Object> response = new HashMap<>();
//
//		try {
//			// Fetch all VisitPage entries
//			List<VisitPage> allVisitPages = visitPageRepository.findAll();
//
//			// Filter entries where visitType is 'website'
//			List<Map<String, Object>> websiteVisitPages = allVisitPages.stream()
//					.filter(visitPage -> "Whatsapp".equalsIgnoreCase(visitPage.getVisitType()))
//					.map(visitPage -> {
//						// Create a map to store visitPageId and data only
//						Map<String, Object> filteredData = new HashMap<>();
//						filteredData.put("visitPageId", visitPage.getVisitPageId());
//						filteredData.put("data", visitPage.getData());
//						return filteredData;
//					})
//					.collect(Collectors.toList());
//
//			// Return the filtered list
//			response.put("websiteVisitPages", websiteVisitPages);
//			return ResponseEntity.ok(response);
//
//		} catch (Exception e) {
//			// Log and handle errors
//			e.printStackTrace();
//			response.put("error", "Failed to retrieve visit types due to server error");
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//	}


	@Override
	public ResponseEntity<?> updatePromoteStatus(PromoteWebModel promoteWebModel) {
		if (promoteWebModel == null || promoteWebModel.getPostId() == null) {
			return ResponseEntity.badRequest().body("Invalid request: Post ID is required.");
		}

		// Fetching the post from the database
		Optional<Posts> optionalPost = postsRepository.findById(promoteWebModel.getPostId());

		if (optionalPost.isPresent()) {
			Posts promotedPost = optionalPost.get();

			// Validate and update promoteFlag
			if (promoteWebModel.getPromoteFlag() != null) {
				promotedPost.setPromoteFlag(promoteWebModel.getPromoteFlag());
			} else {
				return ResponseEntity.badRequest().body("Invalid request: Promote flag cannot be null.");
			}

			// Validate and update promoteStatus
			if (promoteWebModel.getPromoteStatus() != null) {
				promotedPost.setPromoteStatus(promoteWebModel.getPromoteStatus());
			} else {
				return ResponseEntity.badRequest().body("Invalid request: Promote status cannot be null or empty.");
			}

			// Saving the updated post
			postsRepository.save(promotedPost);

			return ResponseEntity.ok("Promote status updated successfully.");
		}

		// Returning a response if the post is not found
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found.");
	}
	
 
    @Override
    public VisitPage addVisitPage(VisitPage visitPage) {
        return visitPageRepository.save(visitPage);
    }
    @Override
    public List<VisitPage> getPagesByCategoryId(Integer categoryId) {
        return visitPageRepository.findByCategory_CategoryId(categoryId);
    }
    
    @Override
    // Get all categories
    public List<VisitePageCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    public ResponseEntity<?> getWebsiteCategories() {
        Map<String, Object> response = new HashMap<>();

        try {
           
			// Fetch all categories
            List<VisitePageCategory> allCategories = categoryRepository.findAll();

            // Filter only categories where visitType = "website"
            List<Map<String, Object>> websiteCategories = allCategories.stream()
                    .filter(category -> "website".equalsIgnoreCase(category.getVisitType()))
                    .map(category -> {
                        Map<String, Object> filteredData = new HashMap<>();
                        filteredData.put("categoryId", category.getCategoryId());
                        filteredData.put("categoryName", category.getCategoryName());
                        filteredData.put("visitType", category.getVisitType());
                        return filteredData;
                    })
                    .collect(Collectors.toList());

            // Return response
            response.put("websiteCategories", websiteCategories);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch website categories due to server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> getWhatsAppCategories() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch all categories
            List<VisitePageCategory> allCategories = categoryRepository.findAll();

            // Filter only categories where visitType = "Whatsapp"
            List<Map<String, Object>> whatsappCategories = allCategories.stream()
                    .filter(category -> "Whatsapp".equalsIgnoreCase(category.getVisitType()))
                    .map(category -> {
                        Map<String, Object> filteredData = new HashMap<>();
                        filteredData.put("categoryId", category.getCategoryId());
                        filteredData.put("categoryName", category.getCategoryName());
                        filteredData.put("visitType", category.getVisitType());
                        return filteredData;
                    })
                    .collect(Collectors.toList());

            // Return response
            response.put("whatsappCategories", whatsappCategories);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch WhatsApp categories due to server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
