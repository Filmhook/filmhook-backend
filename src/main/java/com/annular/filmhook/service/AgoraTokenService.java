package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.AgoraWebModel;

public interface AgoraTokenService {

    String getRTCToken(AgoraWebModel model);

    String getRTMToken(AgoraWebModel model);

    String getChatToken(AgoraWebModel model);

}
