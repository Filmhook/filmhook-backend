package com.annular.filmhook.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.InAppNotification;

@Repository
public interface InAppNotificationRepository extends JpaRepository<InAppNotification, Integer>{

	List<InAppNotification> findByReceiverIdOrderByCreatedOnDesc(Integer userId);

	@Query("SELECT n FROM InAppNotification n WHERE n.id = :shootingLocationChatId")
	List<InAppNotification> findByChatId(Integer shootingLocationChatId);

	@Query("SELECT n FROM InAppNotification n WHERE n.id = :marketPlaceChatId")
	List<InAppNotification> findByChatIds(Integer marketPlaceChatId);

	List<InAppNotification> findByReceiverIdAndCreatedOnAfterOrderByCreatedOnDesc(Integer receiverId, Date createdOn);

	  @Modifying
	    @Transactional
	    @Query("UPDATE InAppNotification n SET n.isDeleted = true WHERE n.inAppNotificationId IN (:ids)")
	    void softDeleteByIds(@Param("ids") List<Integer> ids);
	  
	  Page<InAppNotification> findByReceiverIdAndCreatedOnBetweenAndIsDeletedFalseOrderByCreatedOnDesc(
			    Integer receiverId, Date startDate, Date endDate, Pageable pageable);


	  // Total unread in the same 30-day window (treat NULL as unread)
	    @Query("SELECT COUNT(n) FROM InAppNotification n " +
	           "WHERE n.receiverId = :receiverId " +
	           "AND n.isDeleted = false " +
	           "AND n.createdOn BETWEEN :startDate AND :endDate " +
	           "AND (n.isRead IS NULL OR n.isRead = false)")
	    long countUnreadInRange(@Param("receiverId") Integer receiverId,
	                            @Param("startDate") Date startDate,
	                            @Param("endDate") Date endDate);

	    // Total unseen since last opened (independent of paging)
	    @Query("SELECT COUNT(n) FROM InAppNotification n " +
	           "WHERE n.receiverId = :receiverId " +
	           "AND n.isDeleted = false " +
	           "AND n.createdOn > :lastOpenedTime")
	    long countUnseenSince(@Param("receiverId") Integer receiverId,
	                          @Param("lastOpenedTime") Date lastOpenedTime);
	
	    @Query(
	            "SELECT COUNT(n) FROM InAppNotification n " +
	            "WHERE n.userType = :userType " +
	            "AND n.id = :refId " +
	            "AND n.isDeleted = false"
	        )
	        int countExpiryReminders(
	                @Param("userType") String userType,
	                @Param("refId") Integer refId
	        );

}
