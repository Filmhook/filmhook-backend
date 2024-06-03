package com.annular.filmhook.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
import com.annular.filmhook.repository.PromoteRepository;
import com.annular.filmhook.service.PromoteService;
import com.annular.filmhook.webmodel.PromoteWebModel;

@Service
public class PromoteServiceImpl implements PromoteService {

	private static final Logger logger = LoggerFactory.getLogger(PromoteServiceImpl.class);

	@Autowired
	PromoteRepository promoteRepository;

	@Autowired
	UserDetails userDetails;

	@Autowired
	PostsRepository postsRepository;

	@Override
	public ResponseEntity<?> addPromote(PromoteWebModel promoteWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("addPromote method start");

			Promote promote = null;
			Integer userId = userDetails.userInfo().getId();

			// Check if a Promote already exists with the same postId and userId
			Promote existingPromote = promoteRepository.findByPostIdAndUserId(promoteWebModel.getPostId(), userId);
			if (existingPromote != null) {
				// Update the existing promote instead of creating a new one
				promote = existingPromote;
				promote.setAmount(promoteWebModel.getAmount());
				promote.setCgst(promoteWebModel.getCgst());
				promote.setPrice(promoteWebModel.getPrice());
				promote.setNumberOfDays(promoteWebModel.getNumberOfDays());
				promote.setTotalCost(promoteWebModel.getTotalCost());
				promote.setTaxFee(promoteWebModel.getTaxFee());
				promote.setSgst(promoteWebModel.getSgst());
				promote.setStatus(!promote.getStatus());
				if (promoteWebModel.getCountry() != null) {
					promote.setCountry(String.join(",", promoteWebModel.getCountry()));
				}
				promote.setUpdatedOn(new Date());
				promote.setUpdatedBy(userId);
				promote.setMultimediaId(promoteWebModel.getMultimediaId());
			} else {
				// Create a new Promote if none exists
				promote = new Promote();
				promote.setAmount(promoteWebModel.getAmount());
				promote.setCgst(promoteWebModel.getCgst());
				promote.setPrice(promoteWebModel.getPrice());
				promote.setPostId(promoteWebModel.getPostId());
				promote.setNumberOfDays(promoteWebModel.getNumberOfDays());
				promote.setTotalCost(promoteWebModel.getTotalCost());
				promote.setTaxFee(promoteWebModel.getTaxFee());
				promote.setSgst(promoteWebModel.getSgst());
				promote.setCreatedBy(userId);
				promote.setCreatedOn(new Date());
				promote.setStatus(true);
				if (promoteWebModel.getCountry() != null) {
					promote.setCountry(String.join(",", promoteWebModel.getCountry()));
				}
				promote.setUserId(userId);
				promote.setMultimediaId(promoteWebModel.getMultimediaId());
			}

			promote = promoteRepository.save(promote);
			response.put("promoteInfo", promote);

			// Updating the promote flag in post-table
			Posts promotedPost = postsRepository.findById(promote.getPostId()).orElse(null);
			if (promotedPost != null) {
				promotedPost.setPromoteFlag(true);
				postsRepository.saveAndFlush(promotedPost);
				Posts demotedPost = postsRepository.findByPromoteFlag(true).orElse(null);
				if (demotedPost != null) {
					demotedPost.setPromoteFlag(false);
					postsRepository.saveAndFlush(demotedPost);
				}
			}
			return ResponseEntity.ok(new Response(1, "Add promote successfully", response));
		} catch (Exception e) {
			logger.error("Error setting Promote: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error setting promote", e.getMessage()));
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
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(-1, "Promote with ID " + promoteId + " not found", null));
			}
			logger.info("updatePromote method end");
		} catch (Exception e) {
			logger.error("Error updating promote: {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to update promote", e.getMessage()));
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Delete promote failed", e.getMessage()));
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to fetch promotes", e.getMessage()));
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
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(-1, "Promote not found", null));
			}
		} catch (Exception e) {
			logger.error("Error in getByPromoteId method: {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to fetch promote", e.getMessage()));
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
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Promotion not found");
		}
	}
}
