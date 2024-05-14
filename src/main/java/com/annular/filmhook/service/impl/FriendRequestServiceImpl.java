package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.FollowersRequest;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.FriendRequestService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.FollowersRequestWebModel;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

	@Autowired
	UserDetails userDetails;

	@Autowired
	FriendRequestRepository friendRequestRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	S3Util s3Util;
	
	@Autowired
	FileUtil fileUtil;
	
	@Autowired
	MediaFilesRepository mediaFilesRepository;
	
//	@Override
//	public ResponseEntity<?> saveFollowersRequest(FollowersRequestWebModel followersRequestWebModel) {
//
//	    try {
//	        Integer senderId = userDetails.userInfo().getId();
//	        Integer receiverId = followersRequestWebModel.getFollowersRequestReceiverId();
//	        String status = followersRequestWebModel.getFollowersRequestSenderStatus();
//
//	        // Log or print the senderId and receiverId to ensure they are set correctly
//	        System.out.println("Sender ID: " + senderId);
//	        System.out.println("Receiver ID: " + receiverId);
//
//	        // Check if senderId and receiverId are not null
//	        if (senderId == null || receiverId == null) {
//	            return ResponseEntity.badRequest().body("Sender ID or Receiver ID is null");
//	        }
//
//	        // Check if the senderId and receiverId are different
//	        if (senderId.equals(receiverId)) {
//	            return ResponseEntity.badRequest().body("Sender ID and Receiver ID cannot be the same");
//	        }
//
//	        // Check if the sender already sent a request to the receiver
//	        Optional<FollowersRequest> existingFriendRequest = friendRequestRepository
//	                .findByFriendRequestSenderIdAndFriendRequestReceiverId(senderId, receiverId);
//
//	        if (existingFriendRequest.isPresent()) {
//	            // If a friend request already exists, return a bad request response
//	            return ResponseEntity.badRequest()
//	                    .body(new Response(0, "Friend request already exists", HttpStatus.BAD_REQUEST));
//	        } else {
//	            // If no existing request, proceed to save the new friend request
//	            FollowersRequest friendRequest = new FollowersRequest();
//	            friendRequest.setFollowersRequestSenderId(senderId);
//	            friendRequest.setFollowersRequestReceiverId(receiverId);
//	            friendRequest.setFollowersRequestSenderStatus("pending");
//	            friendRequest.setFollowersRequestCreatedBy(senderId);
//	            friendRequest.setFollowersRequestIsActive(true);
//
//	            // Log or print the friendRequest object to verify its data before saving
//	            System.out.println("Friend Request to Save: " + friendRequest);
//
//	            FollowersRequest savedFriendRequest = friendRequestRepository.save(friendRequest);
//	            
//	            
//	            FollowersRequest friendRequests = new FollowersRequest();
//	            friendRequests.setFollowersRequestSenderId(receiverId);
//	            friendRequests.setFollowersRequestReceiverId(senderId);
//	            friendRequests.setFollowersRequestSenderStatus("confirm/Reject");
//	            friendRequests.setFollowersRequestCreatedBy(senderId);
//	            friendRequests.setFollowersRequestIsActive(true);
//
//	            FollowersRequest savedFriendRequests = friendRequestRepository.save(friendRequests);
//
//	            // Log or print the savedFriendRequest object to verify its data after saving
//	            System.out.println("Saved Friend Request: " + savedFriendRequest);
//
//	            // Return a success response
//	            return ResponseEntity.ok().body("Friend request saved successfully");
//	        }
//
//	    } catch (Exception e) {
//	        // Log the exception or handle it appropriately
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body("An error occurred while saving the friend request: " + e.getMessage());
//	    }
//	}
	@Override
	public ResponseEntity<?> saveFollowersRequest(FollowersRequestWebModel followersRequestWebModel) {
	    try {
	        Integer senderId = userDetails.userInfo().getId();
	        Integer receiverId = followersRequestWebModel.getFollowersRequestReceiverId();

	        // Log or print senderId and receiverId to ensure they are set correctly
	        System.out.println("Sender ID: " + senderId);
	        System.out.println("Receiver ID: " + receiverId);

	        // Check if senderId and receiverId are not null
	        if (senderId == null || receiverId == null) {
	            return ResponseEntity.badRequest().body("Sender ID or Receiver ID is null");
	        }

	        // Check if senderId and receiverId are different
	        if (senderId.equals(receiverId)) {
	            return ResponseEntity.badRequest().body("Sender ID and Receiver ID cannot be the same");
	        }

	        // Check if the friend request already exists in either direction
	        Optional<FollowersRequest> existingFriendRequest1 = friendRequestRepository
	                .findByFriendRequestSenderIdAndFriendRequestReceiverId(senderId, receiverId);
	        Optional<FollowersRequest> existingFriendRequest2 = friendRequestRepository
	                .findByFriendRequestSenderIdAndFriendRequestReceiverId(receiverId, senderId);

	        if (existingFriendRequest1.isPresent() || existingFriendRequest2.isPresent()) {
	            // If a friend request already exists in either direction, return a bad request response
	            return ResponseEntity.badRequest().body(new Response(0, "Friend request already exists", HttpStatus.BAD_REQUEST));
	        } else {
	            // If no existing request, proceed to save the new friend request
	            FollowersRequest friendRequest = new FollowersRequest();
	            friendRequest.setFollowersRequestSenderId(senderId);
	            friendRequest.setFollowersRequestReceiverId(receiverId);
	            friendRequest.setFollowersRequestStatus("follow");
	            friendRequest.setFollowersRequestCreatedBy(senderId);
	            friendRequest.setFollowersRequestIsActive(true);

	            // Log the friend request object to verify its data before saving
	            System.out.println("Friend Request to Save: " + friendRequest);

	            FollowersRequest savedFriendRequest = friendRequestRepository.save(friendRequest);

	            // Optionally, return a structured response object for success
	            return ResponseEntity.ok().body(new Response(1, "Friend request saved successfully", HttpStatus.OK));
	        }
	    } catch (Exception e) {
	        // Log the exception or handle it appropriately
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred while saving the friend request: " + e.getMessage());
	    }
	}
//	@Override
//	public ResponseEntity<?> updateFriendRequest(FollowersRequestWebModel followersRequestWebModel) {
//	    try {
//	        Integer senderId = userDetails.userInfo().getId();
//	        Integer receiverId = followersRequestWebModel.getFollowersRequestReceiverId();
//	        String newStatus = followersRequestWebModel.getFollowersRequestSenderStatus();
//
//	        System.out.println("senderId"+senderId);
//	        System.out.println("receiverId"+receiverId);
//	        
//	        if (senderId == null || receiverId == null) {
//	            return ResponseEntity.badRequest().body("Sender ID or Receiver ID is null");
//	        }
//	        
//
//	        Optional<FollowersRequest> existingFriendRequestOpt = friendRequestRepository
//	                .findByFriendRequestSenderIdAndFriendRequestReceiverId(senderId, receiverId);
//	        Optional<FollowersRequest> existingFriendRequestOpt2 = friendRequestRepository
//	        		.findByFriendRequestSenderAndFriendRequestReceiverId(receiverId,senderId);
//
//	        if (existingFriendRequestOpt.isPresent()) {
//	            FollowersRequest existingFriendRequest = existingFriendRequestOpt.get();
//
//	            existingFriendRequest.setFollowersRequestSenderStatus(newStatus);//confirm
//	            friendRequestRepository.save(existingFriendRequest);
//	           if(existingFriendRequestOpt2.isPresent())
//	           {
//	        	   FollowersRequest existingFriendRequests = existingFriendRequestOpt.get();
//	        	   existingFriendRequest.getFollowersRequestSenderStatus();
//	        	   if("confirm".equalsIgnoreCase(existingFriendRequest.getFollowersRequestSenderStatus()))
//	        	   {
//	        		   existingFriendRequests.setFollowersRequestSenderStatus("unfollow");//confirm--->unfollow
//	        		   existingFriendRequest.setFollowersRequestSenderStatus("unfollow");
//	        		   
//	        	   }
//	        	   else if("Reject".equalsIgnoreCase(existingFriendRequest.getFollowersRequestSenderStatus()))
//	        	   {
//	        		   existingFriendRequests.setFollowersRequestSenderStatus("follow");
//	        		   existingFriendRequest.setFollowersRequestSenderStatus("follow");
//	        	   }
//	        	    
//	        	   friendRequestRepository.save(existingFriendRequest);
//	           }
//	            
//
//	           // return ResponseEntity.ok().body("Friend request updated successfully");
//	        } else {
//
//	            return ResponseEntity.notFound().build();
//	        }
//	    } catch (Exception e) {
//	        // Log the exception or handle it appropriately
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body("An error occurred while updating the friend request: " + e.getMessage());
//	    }
//	    return ResponseEntity.ok().body("Friend request updated successfully");
//	}
	
	@Override
	public ResponseEntity<?> updateFriendRequest(FollowersRequestWebModel followersRequestWebModel) {
	    try {
	        Integer requestId = followersRequestWebModel.getFollowersRequestId(); // Assuming you have a field for request ID in the model
	        //String newStatus = followersRequestWebModel.getFollowersRequestSenderStatus();

	        // Check if request ID is provided
	        if (requestId == null) {
	            return ResponseEntity.badRequest().body("Request ID is required");
	        }

	        // Retrieve the existing friend request by ID
	        Optional<FollowersRequest> existingRequestOpt = friendRequestRepository.findById(requestId);

	        if (existingRequestOpt.isPresent()) {
	            FollowersRequest existingRequest = existingRequestOpt.get();

	            // Update the status if provided
	            
	                existingRequest.setFollowersRequestStatus("unfollow");
	                existingRequest.setFollowersRequestIsActive(false);
	                
	            

	            // Save the updated request
	            friendRequestRepository.save(existingRequest);

	            return ResponseEntity.ok().body(new Response(1, "Friend request updated successfully", HttpStatus.OK));
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        // Log the exception or handle it appropriately
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred while updating the friend request: " + e.getMessage());
	    }
	}

	@Override
	public ResponseEntity<?> getFriendRequest(Integer userId) {
	    try {
	        // Fetch friend requests for the given user id from the FollowersRequest table
	        List<FollowersRequest> friendRequests = friendRequestRepository.findByFriendRequestSenderIdAndFriendRequestSenderStatus(userId);

	        // List to store the processed friend requests
	        List<Map<String, Object>> updatedFriendRequests = new ArrayList<>();

	        // Variable to store the count of friend requests
	        int friendRequestCount = 0;

	        
	        // Process each friend request
	        for (FollowersRequest friendRequest : friendRequests) {
	            // Create a map to store the details of the friend request
	            Map<String, Object> requestDetails = new LinkedHashMap<>();

	            Integer receiverId = friendRequest.getFollowersRequestReceiverId();

	            
	            
	            // Fetch the user details for the receiver of the friend request
	            Optional<User> userOptional = userRepository.findById(receiverId);
	            //Optional<MediaFiles> mediaFileOptional = mediaFilesRepository.findById(friendRequest.getFollowersRequestReceiverId());

	            if (userOptional.isPresent()) {
	                User receiverUser = userOptional.get();

	                // Populate the request details map
	                requestDetails.put("receiverId", receiverId);
	                requestDetails.put("followersRequestId", friendRequest.getFollowersRequestId());
	                requestDetails.put("userId", friendRequest.getFollowersRequestSenderId());
	                requestDetails.put("status", friendRequest.getFollowersRequestStatus());
	                requestDetails.put("userName", receiverUser.getName()); // Assuming name is the user's name field
	                Optional<MediaFiles> mediaFileOptional = mediaFilesRepository.findById(receiverId);
	                if (mediaFileOptional.isPresent()) {
	                    MediaFiles mediaFiles = mediaFileOptional.get();
	                    requestDetails.put("profilePicUrl", s3Util.getS3BaseURL() + S3Util.S3_PATH_DELIMITER + mediaFiles.getFilePath() + mediaFiles.getFileType());
	                } else {
	                    // Handle case where media data is not found
	                    requestDetails.put("profilePicUrl", null);
	                }
	             // Increment the count of friend requests
	                friendRequestCount++;
	            }

	            // Add the processed friend request details to the list
	            updatedFriendRequests.add(requestDetails);
	        }

	     // Create a response map containing both the friend requests and the count
	        Map<String, Object> responseMap = new HashMap<>();
	        responseMap.put("friendRequests", updatedFriendRequests);
	        responseMap.put("friendRequestCount", friendRequestCount);

	        // Return the response map
	        return ResponseEntity.ok().body(responseMap);
	    } catch (Exception e) {
	        // Log the exception or handle it appropriately
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred while fetching friend requests: " + e.getMessage());
	    }
	}



}
