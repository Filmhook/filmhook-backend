package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.UserSession;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Integer> {

    UserSession findBySessionToken(String sessionToken);

    List<UserSession> findByUserIdAndIsActive(Integer userId, Boolean isActive);

    List<UserSession> findByUserId(Integer userId);
    
    UserSession findByUserIdAndDeviceNameAndIpAddress(Integer userId, String deviceName, String ipAddress);
    
    Optional<UserSession> findByUserIdAndFirebaseToken(Integer userId, String firebaseToken);


}

