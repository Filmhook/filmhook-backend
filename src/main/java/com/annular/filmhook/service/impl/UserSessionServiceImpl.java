package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.UserSession;
import com.annular.filmhook.repository.UserSessionRepository;
import com.annular.filmhook.service.UserSessionService;
import com.annular.filmhook.util.LastSeenService;
import com.annular.filmhook.util.WebSocketService;
@Service
public class UserSessionServiceImpl implements UserSessionService{

	@Autowired
	private UserSessionRepository userSessionRepository;

	@Autowired
	LastSeenService lastSeenService;

	@Autowired
	private WebSocketService ws;


	public UserSession createSession(Integer userId, String firebaseToken, String deviceName, String ip) {


		Optional<UserSession> existingSession =
				userSessionRepository.findByUserIdAndFirebaseToken(userId, firebaseToken);

		String newSessionToken = UUID.randomUUID().toString();

		if (existingSession.isPresent()) {

			UserSession session = existingSession.get();
			session.setIsActive(true);
			session.setLastUsedOn(new Date());
			session.setSessionToken(newSessionToken);
			session.setIpAddress(ip);
			session.setDeviceName(deviceName);

			return userSessionRepository.save(session);

		} else {
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

	@Transactional
	public void logoutSpecificDevice(String targetSessionToken) {

		UserSession targetSession =
				userSessionRepository.findBySessionToken(targetSessionToken);

		if (targetSession == null) {
			System.out.println("❌ Session not found");
			return;
		}

		Integer userId = targetSession.getUserId();

		// 1️⃣ Mark session inactive
		targetSession.setIsActive(false);
		userSessionRepository.save(targetSession);

		System.out.println("🚪 Remote logout for session: " + targetSessionToken);

		// 2️⃣ Send WebSocket event ONLY to that session's user
		ws.notifyUser(userId, "FORCE_LOGOUT",
				Map.of(
						"sessionToken", targetSessionToken,
						"message", "You were logged out from another device"
						));
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

	public List<Map<String, Object>> getActiveDevices(Integer userId) {

		List<UserSession> sessions =
				userSessionRepository.findByUserIdAndIsActive(userId, true);

		List<Map<String, Object>> result = new ArrayList<>();

		for (UserSession session : sessions) {

			Map<String, Object> map = new HashMap<>();
			map.put("deviceName", session.getDeviceName());
			map.put("ipAddress", session.getIpAddress());
			map.put("sessionToken", session.getSessionToken());
			map.put("createdOn", session.getCreatedOn());
			map.put("lastUsedOn", session.getLastUsedOn());

			// Human readable last-active time (like Instagram)
			String formattedLastSeen = lastSeenService.formatLastSeen(session.getLastUsedOn());
			map.put("lastSeen", formattedLastSeen);

			result.add(map);
		}

		return result;
	}


}
