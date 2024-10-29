package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.InAppNotification;

@Repository
public interface InAppNotificationRepository extends JpaRepository<InAppNotification, Integer>{

	List<InAppNotification> findByReceiverIdOrderByCreatedOnDesc(Integer userId);

	
	

}
