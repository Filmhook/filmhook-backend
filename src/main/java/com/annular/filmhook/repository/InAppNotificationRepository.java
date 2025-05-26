package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.InAppNotification;

@Repository
public interface InAppNotificationRepository extends JpaRepository<InAppNotification, Integer>{

	List<InAppNotification> findByReceiverIdOrderByCreatedOnDesc(Integer userId);

	@Query("SELECT n FROM InAppNotification n WHERE n.id = :shootingLocationChatId")
	List<InAppNotification> findByChatId(Integer shootingLocationChatId);

	@Query("SELECT n FROM InAppNotification n WHERE n.id = :marketPlaceChatId")
	List<InAppNotification> findByChatIds(Integer marketPlaceChatId);

	

	
	

}
