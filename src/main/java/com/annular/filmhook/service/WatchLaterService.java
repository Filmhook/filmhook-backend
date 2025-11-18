package com.annular.filmhook.service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.webmodel.PostWebModel;

import java.util.List;

public interface WatchLaterService {
	Response toggleWatchLater(Integer userId, Integer postId) ;

	  Response getActiveWatchLaterPosts(String userId);
    
}
