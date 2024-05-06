package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.FCMRequestWebModel;

public interface FcmService {

//	void sendFCMMessage(String fcmToken, String userName, String callType, String userId, String channelName,
//			String channelToken);

	void sendFCMMessage(FCMRequestWebModel request);

}
