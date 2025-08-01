package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.UserSearchHistory;
@Repository
public interface UserSearchHistoryRepository extends JpaRepository<UserSearchHistory, Integer> {

    Optional<UserSearchHistory> findByUserIdAndSearchedUserIdAndSource(Integer userId, Integer searchedUserId, String source);

    List<UserSearchHistory> findByUserIdAndSourceOrderBySearchedAtDesc(Integer userId, String source);
    List<UserSearchHistory> findByUserId(Integer userId);
    long countByUserIdAndSourceAndPinProfileTrue(Integer userId, String source);

}