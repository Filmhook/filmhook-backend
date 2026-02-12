package com.annular.filmhook.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.UserSession;

public interface UserSessionService {
	
	 public UserSession createSession(Integer userId, String firebaseToken, String deviceName, String ip);
	 
	 public void updateLastUsed(String sessionToken);
	 
	 public void logoutSession(String sessionToken);
	 
	 public void logoutAll(Integer userId);

	public Date getLastActiveTime(Integer userId);

	public Response logoutSpecificDevice(Integer userId, String deviceName, String ipAddress);
	
	public List<Map<String, Object>> getActiveDevices(Integer userId);

}
