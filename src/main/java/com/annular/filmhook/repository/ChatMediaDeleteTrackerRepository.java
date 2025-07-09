package com.annular.filmhook.repository;

import com.annular.filmhook.model.ChatMediaDeleteTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ChatMediaDeleteTrackerRepository extends JpaRepository<ChatMediaDeleteTracker, Integer> {

    List<ChatMediaDeleteTracker> findByUserIdAndChatId(Integer userId, Integer chatId);

    @Query("SELECT c.mediaFileId FROM ChatMediaDeleteTracker c WHERE c.userId = :userId AND c.chatId = :chatId AND c.deleted = true")
    List<Integer> findDeletedMediaIdsByUserIdAndChatId(Integer userId, Integer chatId);
}
