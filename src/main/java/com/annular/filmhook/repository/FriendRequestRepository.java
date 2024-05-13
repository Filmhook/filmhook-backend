package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FollowersRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FollowersRequest, Integer> {

	@Query("SELECT f FROM FollowersRequest f WHERE f.followersRequestSenderId = :senderId AND f.followersRequestReceiverId = :receiverId")
	Optional<FollowersRequest> findByFriendRequestSenderIdAndFriendRequestReceiverId(Integer senderId, Integer receiverId);

	@Query("SELECT f FROM FollowersRequest f WHERE f.followersRequestReceiverId = :senderId AND f.followersRequestSenderId = :receiverId")
	Optional<FollowersRequest> findByFriendRequestSenderAndFriendRequestReceiverId(Integer receiverId, Integer senderId);

	@Query("SELECT f FROM FollowersRequest f WHERE f.followersRequestSenderId = :userId and f.followersRequestIsActive = true")
	List<FollowersRequest> findByFriendRequestSenderIdAndFriendRequestSenderStatus(Integer userId );



}
