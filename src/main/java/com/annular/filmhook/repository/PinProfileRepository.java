package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.UserProfilePin;

@Repository
public interface PinProfileRepository extends JpaRepository<UserProfilePin, Integer> {

    @Query("select up from UserProfilePin up where up.userId=:id and up.status=true")
    List<UserProfilePin> findByUserId(Integer id);

    @Query("select up from UserProfilePin up where up.userId=:userId and up.pinProfileId =:pinProfileId")
    Optional<UserProfilePin> findByUserIdAndPinProfileId(Integer userId, Integer pinProfileId);

    Optional<UserProfilePin> findByPinProfileIdAndUserId(Integer pinProfileId, Integer userId);


}
