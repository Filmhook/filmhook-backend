package com.annular.filmhook.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ShootingLocationChat;

@Repository
public interface ShootingLocationChatRepository extends JpaRepository<ShootingLocationChat, Integer> {

	boolean existsByShootingLocationSenderIdAndShootingLocationReceiverIdAndAcceptTrue(Integer userId, Integer userId2);

	@Query("SELECT DISTINCT c.shootingLocationReceiverId FROM ShootingLocationChat c WHERE c.shootingLocationSenderId = :senderId")
	List<Integer> findDistinctReceiverIdsBySenderId(@Param("senderId") Integer senderId);

	@Query("SELECT DISTINCT c.shootingLocationSenderId FROM ShootingLocationChat c WHERE c.shootingLocationReceiverId = :receiverId")
	List<Integer> findDistinctSenderIdsByReceiverId(@Param("receiverId") Integer receiverId);


    @Query("SELECT c FROM ShootingLocationChat c WHERE " +
    	       "(c.shootingLocationSenderId = :senderId AND c.shootingLocationReceiverId = :receiverId) " +
    	       "OR (c.shootingLocationSenderId = :receiverId AND c.shootingLocationReceiverId = :senderId)")
    	List<ShootingLocationChat> findByShootingLocationSenderIdAndShootingLocationReceiverIdOrViceVersa(
    	        @Param("senderId") Integer senderId, 
    	        @Param("receiverId") Integer receiverId);

    @Query("SELECT c FROM ShootingLocationChat c WHERE c.shootingLocationSenderId = :userId OR c.shootingLocationReceiverId = :userId")
    List<ShootingLocationChat> findByUserIdInSenderOrReceiver(@Param("userId") Integer userId);



}
