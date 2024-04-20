package com.annular.filmhook.service;

import com.annular.filmhook.model.AgoraWebModel;

public interface AgoraTokenService {

    String getAgoraRTCToken(AgoraWebModel agoraWebModel);

    String getAgoraRTMToken(AgoraWebModel agoraWebModel);

}
