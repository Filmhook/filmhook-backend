package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.PostView;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.User;

public interface PostViewRepository extends JpaRepository<PostView, Integer> {
    boolean existsByPostAndUser(Posts post, User user);
    
    PostView findTopByPostAndUserOrderByViewedAtDesc(Posts post, User user);
}

