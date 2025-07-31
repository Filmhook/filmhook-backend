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
    public void saveSearchHistory(Integer userId, Integer searchedUserId, String source) {
        if (userId.equals(searchedUserId)) return;

        Optional<UserSearchHistory> existingOpt = userSearchHistoryRepo
                .findByUserIdAndSearchedUserIdAndSource(userId, searchedUserId, source);

        UserSearchHistory history;

        if (existingOpt.isPresent()) {
            // ✅ Update the existing record — carry forward the ID
            history = existingOpt.get();
            history.setSearchedAt(LocalDateTime.now());
        } else {
            // ✅ Build new record
            history = UserSearchHistory.builder()
                    .userId(userId)
                    .searchedUserId(searchedUserId)
                    .source(source)
                    .searchedAt(LocalDateTime.now())
                    .build();
        }

        // ✅ Hibernate will insert or update depending on whether ID is set
        userSearchHistoryRepo.save(history);
    }

    @Override
    public List<RecentUserWebModel> getRecentUserActivities(Integer userId) {
        Map<String, Map<Integer, RecentUserWebModel>> categorizedMap = new HashMap<>();
        categorizedMap.put("search", new LinkedHashMap<>());
        categorizedMap.put("chat", new LinkedHashMap<>());

        for (String source : List.of("search", "chat")) {
            List<UserSearchHistory> historyList = userSearchHistoryRepo.findByUserIdAndSourceOrderBySearchedAtDesc(userId, source);
            for (UserSearchHistory history : historyList) {
                userRepo.findById(history.getSearchedUserId()).ifPresent(user -> {
                    String profilePic = getProfilePic(user.getUserId());
                    categorizedMap.get(source).put(user.getUserId(), RecentUserWebModel.builder()
                            .userId(user.getUserId())
                            .name(user.getName())
                            .userType(user.getUserType())
                            .profilePic(profilePic)
                            .source(source)
                            .lastInteractionTime(history.getSearchedAt())
                            .review(user.getUserType())
                            .build());
                });
            }
        }

        // Combine both types into one list
        List<RecentUserWebModel> combined = new ArrayList<>();
        combined.addAll(categorizedMap.get("search").values());
        combined.addAll(categorizedMap.get("chat").values());

        // Sort by time descending
        return combined.stream()
                .sorted(Comparator.comparing(RecentUserWebModel::getLastInteractionTime).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteSearchHistory(Integer userId, Integer targetUserId, String source) {
        Optional<UserSearchHistory> historyOpt =
                userSearchHistoryRepo.findByUserIdAndSearchedUserIdAndSource(userId, targetUserId, source);
        historyOpt.ifPresent(userSearchHistoryRepo::delete);
    }

    @Override
    public void deleteAllSearchHistory(Integer userId) {
        List<UserSearchHistory> allHistories = userSearchHistoryRepo.findByUserId(userId);
        userSearchHistoryRepo.deleteAll(allHistories);
    }

    private String getProfilePic(Integer userId) {
        String profilePicUrl = userService.getProfilePicUrl(userId);
        return (profilePicUrl != null && !profilePicUrl.isEmpty()) ? profilePicUrl : null;
    }
}
