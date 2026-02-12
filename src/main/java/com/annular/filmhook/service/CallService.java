package com.annular.filmhook.service;


import com.annular.filmhook.Response;
import com.annular.filmhook.webmodel.EndCallRequest;
import com.annular.filmhook.webmodel.StartCallRequest;

public interface CallService {

	Response startCall(StartCallRequest req);

	Response endCall(EndCallRequest req);

	Response getRtcTokenByChannel(String channelName);

	void sendTestNotification(String deviceToken, String title, String body);

}
