package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.model.CallLog;

@Repository
public interface CallLogRepository extends JpaRepository<CallLog, Integer> {

    @Query("SELECT COUNT(c) > 0 FROM CallLog c WHERE c.receiverId = :uid AND c.status = 'initiated'")
    boolean isUserInActiveCall(@Param("uid") Integer uid);
    
//    @Query("SELECT c FROM CallLog c WHERE c.callerId = :userId OR c.receiverId = :userId ORDER BY c.startTime DESC")
//    List<CallLog> findCallHistory(@Param("userId") Integer userId);

    CallLog findByChannelName(String channelName);
    
    boolean existsByReceiverIdAndStatusIn(Integer receiverId, List<String> status);
    
    @Transactional
    @Modifying
    @Query("UPDATE CallLog c SET c.deletedForCaller = true "
         + "WHERE c.callerId = :userId AND c.id IN :callIds")
    void softDeleteCallerCalls(@Param("callIds") List<Integer> callIds,
                               @Param("userId") Integer userId);


    @Transactional
    @Modifying
    @Query("UPDATE CallLog c SET c.deletedForReceiver = true "
         + "WHERE c.receiverId = :userId AND c.id IN :callIds")
    void softDeleteReceiverCalls(@Param("callIds") List<Integer> callIds,
                                 @Param("userId") Integer userId);


    @Transactional
    @Modifying
    @Query("UPDATE CallLog c SET c.deletedForCaller = true "
         + "WHERE c.callerId = :userId")
    void softDeleteAllCallerCalls(@Param("userId") Integer userId);


    @Transactional
    @Modifying
    @Query("UPDATE CallLog c SET c.deletedForReceiver = true "
         + "WHERE c.receiverId = :userId")
    void softDeleteAllReceiverCalls(@Param("userId") Integer userId);


    /* FETCH CALL HISTORY IGNORING DELETED */

    @Query("SELECT c FROM CallLog c "
         + "WHERE (c.callerId = :userId AND c.deletedForCaller = false) "
         + "OR (c.receiverId = :userId AND c.deletedForReceiver = false) "
         + "ORDER BY c.startTime DESC")
    List<CallLog> findCallHistory(@Param("userId") Integer userId);
    
    
}
