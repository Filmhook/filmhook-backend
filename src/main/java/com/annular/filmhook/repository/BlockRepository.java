package com.annular.filmhook.repository;

import java.util.List;

import com.annular.filmhook.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Block;

@Repository
public interface BlockRepository extends JpaRepository<Block, Integer> {

    List<Block> findByBlockedByAndBlockStatus(User userId, String blockStatus);

    Block findByBlockedByAndBlockedUser(User userId, User blockedUser);

    Block findByBlockedByAndBlockedUserAndBlockStatus(User userId, User blockedUser, String blockStatus);

}
