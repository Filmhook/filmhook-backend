package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.UserSearchHistory;

@Repository
public interface UserSearchHistoryRepository extends JpaRepository<UserSearchHistory, Integer> {
    List<UserSearchHistory> findByUserIdOrderBySearchedAtDesc(Integer userId);

    Optional<UserSearchHistory> findByUserIdAndSearchedUserId(Integer userId, Integer searchedUserId);
}
