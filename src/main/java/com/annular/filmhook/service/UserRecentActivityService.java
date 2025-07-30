package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.RecentUserWebModel;

import java.util.List;

public interface UserRecentActivityService {
    void saveSearchHistory(Integer userId, Integer searchedUserId);
    List<RecentUserWebModel> getRecentUserActivities(Integer userId);
}
