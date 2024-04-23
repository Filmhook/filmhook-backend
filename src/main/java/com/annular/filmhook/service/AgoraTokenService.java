package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.AgoraWebModel;

public interface AgoraTokenService {

    String getAgoraRTCToken(AgoraWebModel agoraWebModel);

    String getAgoraRTMToken(AgoraWebModel agoraWebModel);

    String getAgoraChatToken(AgoraWebModel agoraWebModel);

}
