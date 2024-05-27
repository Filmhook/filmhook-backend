package com.annular.filmhook.repository;

import com.annular.filmhook.model.Posts;

import com.annular.filmhook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {

    List<Posts> findByUser(User userId);

    Optional<Posts> findByPromoteFlag(Boolean promoteFlag);

    Posts findByPostId(String postId);

	//List<Posts> findByPromoteFlagAndUserId(boolean b, Integer id);
}
