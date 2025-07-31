package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.RecentUserWebModel;

import java.util.List;

public interface UserRecentActivityService {
    void saveSearchHistory(Integer userId, Integer searchedUserId, String source);
    List<RecentUserWebModel> getRecentUserActivities(Integer userId);
    void deleteSearchHistory(Integer userId, Integer targetUserId, String source); // Single delete
    void deleteAllSearchHistory(Integer userId); // Delete all
}
