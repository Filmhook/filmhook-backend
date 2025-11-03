package com.annular.filmhook.service.impl;

import com.annular.filmhook.Response;
import com.annular.filmhook.controller.LiveSubscribeController;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.WatchLater;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.WatchLaterRepository;
import com.annular.filmhook.service.WatchLaterService;
import com.annular.filmhook.webmodel.PostWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WatchLaterServiceImpl implements WatchLaterService {
	
	  public static final Logger logger = LoggerFactory.getLogger(WatchLaterServiceImpl.class);
	 @Autowired
	    private WatchLaterRepository watchLaterRepository;

	    @Autowired
	    private PostsRepository postsRepository;

	    @Autowired
	    private UserRepository userRepository;
	    @Autowired
	    private PostServiceImpl   postServiceImpl;
	  
	    
	    @Override
	    public Response toggleWatchLater(Integer userId, Integer postId) {
	        logger.info("Toggle Watch Later start => userId: {}, postId: {}", userId, postId);

	        try {
	            User user = userRepository.getUserByUserId(userId).orElse(null);
	            Posts post = postsRepository.findById(postId).orElse(null);

	            if (user == null || post == null) {
	                logger.warn("User or Post not found: userId={}, postId={}", userId, postId);
	                return new Response(0, "User or Post not found", null);
	            }

	            Optional<WatchLater> existingOpt = watchLaterRepository.findByUserAndPost(user, post);

	            if (existingOpt.isPresent()) {
	                WatchLater existing = existingOpt.get();
	                existing.setStatus(!existing.getStatus()); // Toggle the current status
	                watchLaterRepository.save(existing);

	                String message = existing.getStatus()
	                        ? "Added to Watch Later"
	                        : "Removed from Watch Later";

	                logger.info(message);
	                return new Response(1, message, null);
	            } else {
	                WatchLater newEntry = WatchLater.builder()
	                        .user(user)
	                        .post(post)
	                        .status(true)
	                        .build();
	                watchLaterRepository.save(newEntry);

	                logger.info("Added to Watch Later");
	                return new Response(1, "Added to Watch Later", null);
	            }

	        } catch (Exception e) {
	            logger.error("Error in toggleWatchLater: {}", e.getMessage(), e);
	            return new Response(-1, "Error while toggling Watch Later", e.getMessage());
	        }
	    }


	    @Override
	    public Response getActiveWatchLaterPosts(String userId) {
	        try {
	            Integer userIdInt = Integer.valueOf(userId);
	            User user = userRepository.getUserByUserId(userIdInt).orElse(null);

	            if (user == null) {
	                return new Response(0, "User not found", null);
	            }

	            List<Posts> postList = watchLaterRepository.findByUserAndStatus(user, true)
	                    .stream()
	                    .map(WatchLater::getPost)
	                    .collect(Collectors.toList());

	            if (postList.isEmpty()) {
	                return new Response(1, "No posts found in Watch Later", List.of());
	            }

	            List<PostWebModel> postWebModels = postServiceImpl.transformPostsDataToPostWebModel(postList);
	            return new Response(1, "Active Watch Later posts fetched successfully", postWebModels);

	        } catch (Exception e) {
	            return new Response(-1, "Error fetching Watch Later posts: " + e.getMessage(), null);
	        }
	    }


	}