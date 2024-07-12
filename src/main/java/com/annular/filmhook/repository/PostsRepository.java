package com.annular.filmhook.repository;

import com.annular.filmhook.model.Posts;

import com.annular.filmhook.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {

    @Query("SELECT p FROM Posts p WHERE p.user=:userId AND p.status=true ORDER BY p.createdOn DESC")
    List<Posts> getUserPosts(User userId);

    Optional<Posts> findByPromoteFlag(Boolean promoteFlag);

    Posts findByPostId(String postId);

    @Query("SELECT p FROM Posts p WHERE p.user = :userId AND p.promoteStatus = true")
    List<Posts> findByUsers(User userId);

    List<Posts> findAllByPromoteFlag(boolean b);

    @Query("SELECT p FROM Posts p WHERE p.status = true ORDER BY p.createdOn DESC")
    List<Posts> getAllActivePosts(Pageable paging);

    @Query("SElECT p FROM Posts p where p.id = :list and p.user.userId = :userId")
	Optional<Posts> findByIdAndUserId(List<Integer> list, Integer userId);

    // List<Posts> findByPromoteFlagAndUserId(boolean b, Integer id);
}
