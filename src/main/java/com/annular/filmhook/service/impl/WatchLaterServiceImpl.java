package com.annular.filmhook.service.impl;

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
	    public String toggleWatchLater(Integer userId, Integer postId) {
	    	
	    	 logger.info("Watch history controller start {} {}",userId,postId );
	        // ✅ convert userId to Integer for your existing query
	        Integer userIdInt = Integer.valueOf(userId);

	        User user = userRepository.getUserByUserId(userIdInt)
	                .orElse(null);
	        logger.info("Watch history servise user {}",user );
	        
	        Posts post = postsRepository.findById(postId).orElse(null);

	        logger.info("Watch history servise post {}",post );
	        if (user == null || post == null) {
	            return "User or Post not found";
	        }

	        var existingOpt = watchLaterRepository.findByUserAndPost(user, post);

	        if (existingOpt.isPresent()) {
	            WatchLater existing = existingOpt.get();
	            existing.setStatus(!existing.getStatus()); // toggle
	            watchLaterRepository.save(existing);
	            return existing.getStatus() ? "Added to Watch Later" : "Removed from Watch Later";
	        } else {
	            WatchLater newEntry = WatchLater.builder()
	                    .user(user)
	                    .post(post)
	                    .status(true)
	                    .build();
	            watchLaterRepository.save(newEntry);
	            return "Added to Watch Later";
	        }
	    }

	    @Override
	    public List<PostWebModel> getActiveWatchLaterPosts(String userId) {
	        Integer userIdInt = Integer.valueOf(userId);

	        User user = userRepository.getUserByUserId(userIdInt)
	                .orElse(null);
	        if (user == null) return List.of();

	        // Get active watch later posts
	        List<Posts> postList = watchLaterRepository.findByUserAndStatus(user, true)
	                .stream()
	                .map(WatchLater::getPost)
	                .collect(Collectors.toList());

	        // Convert Posts → PostWebModel (using your existing transformer)
	        return postServiceImpl.transformPostsDataToPostWebModel(postList);
	    }
	}