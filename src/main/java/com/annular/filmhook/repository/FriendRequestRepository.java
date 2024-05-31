package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FollowersRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FollowersRequest, Integer> {

	Optional<FollowersRequest> findByFollowersRequestSenderIdAndFollowersRequestReceiverId(Integer senderId, Integer receiverId);

	List<FollowersRequest> findByFollowersRequestReceiverIdAndFollowersRequestIsActive(Integer userId, Boolean status);

	List<FollowersRequest> findByFollowersRequestSenderIdAndFollowersRequestIsActive(Integer userId, Boolean status);

	@Query("SELECT COUNT(f) FROM FollowersRequest f WHERE f.followersRequestSenderId = :userId AND f.followersRequestStatus = 'followed'")
	int countByFollowersRequestReceiverId(Integer userId);

}
