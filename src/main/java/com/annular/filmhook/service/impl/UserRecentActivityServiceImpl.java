package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.UserSearchHistory;
import com.annular.filmhook.repository.ChatMessageRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.UserSearchHistoryRepository;
import com.annular.filmhook.service.UserRecentActivityService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.RecentUserWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserRecentActivityServiceImpl implements UserRecentActivityService {

    @Autowired
    private UserSearchHistoryRepository userSearchHistoryRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ChatMessageRepository chatMessageRepo;

    @Autowired
    private UserService userService;

    @Override
    public void saveSearchHistory(Integer userId, Integer searchedUserId) {
        if (userId.equals(searchedUserId)) return;

        Optional<UserSearchHistory> existing = userSearchHistoryRepo.findByUserIdAndSearchedUserId(userId, searchedUserId);
        UserSearchHistory history = existing.orElse(UserSearchHistory.builder()
                .userId(userId)
                .searchedUserId(searchedUserId)
                .build());
        history.setSearchedAt(LocalDateTime.now());

        userSearchHistoryRepo.save(history);
    }

    @Override
    public List<RecentUserWebModel> getRecentUserActivities(Integer userId) {
        Map<Integer, RecentUserWebModel> recentMap = new HashMap<>();

        // Search history
        for (UserSearchHistory history : userSearchHistoryRepo.findByUserIdOrderBySearchedAtDesc(userId)) {
            userRepo.findById(history.getSearchedUserId().intValue()).ifPresent(user -> {
                String profilePic = getProfilePic(user.getUserId());
                recentMap.put(user.getUserId(), RecentUserWebModel.builder()
                        .userId(user.getUserId())
                        .name(user.getName())
                        .userType(user.getUserType())
                        .profilePic(profilePic)
                        .source("search")
                        .lastInteractionTime(history.getSearchedAt())
                        .build());
            });
        }

        // Chat history
        List<Integer> chatUserIds = chatMessageRepo.findRecentChatPartnerIds(userId);
        for (Integer chatUserId : chatUserIds) {
            if (!chatUserId.equals(userId)) {
                userRepo.findById(chatUserId.intValue()).ifPresent(user -> {
                    String profilePic = getProfilePic(user.getUserId());
                    RecentUserWebModel existing = recentMap.get(user.getUserId());

                    LocalDateTime chatTime = LocalDateTime.now(); // TODO: Replace with actual last chat time if available
                    RecentUserWebModel model = RecentUserWebModel.builder()
                            .userId(user.getUserId())
                            .name(user.getName())
                            .userType(user.getUserType())
                            .profilePic(profilePic)
                            .source("chat")
                            .lastInteractionTime(chatTime)
                            .build();

                    // Update only if new or newer timestamp
                    if (existing == null || existing.getLastInteractionTime().isBefore(chatTime)) {
                        recentMap.put(user.getUserId(), model);
                    }
                });
            }
        }

        return recentMap.values().stream()
                .sorted(Comparator.comparing(RecentUserWebModel::getLastInteractionTime).reversed())
                .collect(Collectors.toList());
    }

    private String getProfilePic(Integer userId) {
        String profilePicUrl = userService.getProfilePicUrl(userId);
        return (profilePicUrl != null && !profilePicUrl.isEmpty()) ? profilePicUrl : null;
    }
}
