package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FollowersRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FollowersRequest, Integer> {

	@Query("SELECT f FROM FollowersRequest f WHERE f.follwersRequestSenderId = :senderId AND f.follwersRequestReceiverId = :receiverId")
	Optional<FollowersRequest> findByFrientRequestSenderIdAndFriendRequestReceiverId(Integer senderId,
			Integer receiverId);

	@Query("SELECT f FROM FollowersRequest f WHERE f.follwersRequestReceiverId = :senderId AND f.follwersRequestSenderId = :receiverId")
	Optional<FollowersRequest> findByfriendRequestSenderAndFriendRequestReceiverId(Integer receiverId,
			Integer senderId);

	@Query("SELECT f FROM FollowersRequest f WHERE f.follwersRequestSenderId = :userId and f.follwersRequestIsActive = true")
	List<FollowersRequest> findByFrientRequestSenderIdAndFriendRequestSenderStatus(Integer userId
			);



}
