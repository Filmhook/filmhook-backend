package com.annular.filmhook.service;

import com.annular.filmhook.model.Posts;
import com.annular.filmhook.webmodel.PostWebModel;

import java.util.List;

public interface WatchLaterService {
    String toggleWatchLater(Integer userId, Integer id);
    List<PostWebModel>  getActiveWatchLaterPosts(String userId);
}
