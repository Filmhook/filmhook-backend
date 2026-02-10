package com.annular.filmhook.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.UserSession;
import com.annular.filmhook.repository.UserSessionRepository;
import com.annular.filmhook.service.UserSessionService;
@Service
public class UserSessionServiceImpl implements UserSessionService{

	@Autowired
	private UserSessionRepository userSessionRepository;

	public UserSession createSession(Integer userId, String firebaseToken, String deviceName, String ip) {

		UserSession session = UserSession.builder()
				.userId(userId)
				.sessionToken(UUID.randomUUID().toString())
				.firebaseToken(firebaseToken)
				.deviceName(deviceName)
				.ipAddress(ip)
				.isActive(true)
				.createdOn(new Date())
				.lastUsedOn(new Date())
				.build();

		return userSessionRepository.save(session);
	}

	public void updateLastUsed(String sessionToken) {
	    UserSession session = userSessionRepository.findBySessionToken(sessionToken);
	    if (session != null && Boolean.TRUE.equals(session.getIsActive())) {
	        session.setLastUsedOn(new Date());
	        userSessionRepository.save(session);
	    }
	}
	
	public Date getLastActiveTime(Integer userId) {
	    List<UserSession> sessions = userSessionRepository.findByUserIdAndIsActive(userId, true);

	    if (sessions.isEmpty()) {
	        return null;
	    }

	    // Find latest last_used_on among devices
	    return sessions.stream()
	            .map(UserSession::getLastUsedOn)
	            .filter(Objects::nonNull)
	            .max(Date::compareTo)
	            .orElse(null);
	}


	public void logoutSession(String sessionToken) {
		UserSession session = userSessionRepository.findBySessionToken(sessionToken);
		if (session != null) {
			session.setIsActive(false);
			session.setFirebaseToken(null);
			session.setLastUsedOn(new Date());
			userSessionRepository.save(session);
		}
	}

	public void logoutAll(Integer userId) {
		List<UserSession> sessions = userSessionRepository.findByUserId(userId);
		for (UserSession s : sessions) {
			s.setIsActive(false);
			s.setFirebaseToken(null);
			s.setLastUsedOn(new Date());
		}
		userSessionRepository.saveAll(sessions);
	}
	
	public Response logoutSpecificDevice(Integer userId, String deviceName, String ipAddress) {

	    UserSession session = userSessionRepository
	            .findByUserIdAndDeviceNameAndIpAddress(userId, deviceName, ipAddress);

	    if (session == null) {
	        return new Response(-1, "Device session not found", null);
	    }

	    session.setIsActive(false);
	    session.setFirebaseToken(null);  // remove push token
	    session.setLastUsedOn(new Date());
	    userSessionRepository.save(session);

	    return new Response(1, "Device logged out successfully", null);
	}


}
