package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FriendRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

	@Query("SELECT f FROM FriendRequest f WHERE (f.frientRequestSenderId = :senderId AND f.friendRequestReceiverId = :receiverId) OR (f.frientRequestSenderId = :receiverId AND f.friendRequestReceiverId = :senderId)")
	Optional<FriendRequest> findByFrientRequestSenderIdAndFriendRequestReceiverId(Integer senderId, Integer receiverId);

	@Query("SELECT  f FROM FriendRequest f where f.frientRequestSenderId = :senderId AND f.friendRequestSenderStatus = :friendRequestStatus ")
    List<FriendRequest> findByFrientRequestSenderIdAndFriendRequestSenderStatus(
            Integer senderId, 
            String friendRequestStatus
    );


	



}

