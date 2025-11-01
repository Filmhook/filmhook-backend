package com.annular.filmhook.repository;

import com.annular.filmhook.model.WatchLater;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchLaterRepository extends JpaRepository<WatchLater, Long> {

    Optional<WatchLater> findByUserAndPost(User user, Posts post);

    List<WatchLater> findByUserAndStatus(User user, Boolean status);
}
