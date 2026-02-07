package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.CallLog;

@Repository
public interface CallLogRepository extends JpaRepository<CallLog, Integer> {

    @Query("SELECT COUNT(c) > 0 FROM CallLog c WHERE c.receiverId = :uid AND c.status = 'initiated'")
    boolean isUserInActiveCall(@Param("uid") Integer uid);

    CallLog findByChannelName(String channelName);
}
