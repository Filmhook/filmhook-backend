package com.annular.filmhook.service;


import com.annular.filmhook.Response;
import com.annular.filmhook.webmodel.CallHistoryDTO;
import com.annular.filmhook.webmodel.EndCallRequest;
import com.annular.filmhook.webmodel.GroupCallEndRequest;
import com.annular.filmhook.webmodel.GroupCallInviteRequest;
import com.annular.filmhook.webmodel.GroupCallJoinRequest;
import com.annular.filmhook.webmodel.GroupCallStartRequest;
import com.annular.filmhook.webmodel.StartCallRequest;

public interface CallService {

	Response startCall(StartCallRequest req);

	Response endCall(EndCallRequest req);

	Response getRtcTokenByChannel(String channelName);

	void sendTestNotification(String deviceToken, String title, String body);

	Response startGroupCall(GroupCallStartRequest req);

	Response joinGroupCall(GroupCallJoinRequest req);

	Response leaveGroupCall(GroupCallJoinRequest req);

	Response endGroupCall(GroupCallEndRequest req);

	Response inviteToGroup(GroupCallInviteRequest req);
	
	Response getCallHistory(Integer userId);

	Response acceptOneToOne(StartCallRequest request);
	
	Response clearCallHistory(CallHistoryDTO req);


}
