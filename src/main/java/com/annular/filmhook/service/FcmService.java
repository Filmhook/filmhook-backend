package com.annular.filmhook.service;

import com.annular.filmhook.model.CallLog;
import com.annular.filmhook.webmodel.FCMRequestWebModel;

public interface FcmService {

//	void sendFCMMessage(String fcmToken, String userName, String callType, String userId, String channelName, String channelToken);

    void sendFCMMessage(FCMRequestWebModel request);

	void sendCallStatusNotification(CallLog log, Integer userId, String status, String token);

	void sendIncomingCallNotification(Integer callerId, Integer receiverId, String callType, String channelName,
			String deviceToken, String callerName, String callerPicUrl);

}
