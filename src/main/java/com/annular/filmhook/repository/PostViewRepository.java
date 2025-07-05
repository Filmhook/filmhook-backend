package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.PostView;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.User;
import java.util.Optional;


public interface PostViewRepository extends JpaRepository<PostView, Long> {
    Optional<PostView> findByPostAndUser(Posts post, User user);
}
