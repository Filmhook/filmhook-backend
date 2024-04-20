package com.annular.filmhook.service.impl;

import com.annular.filmhook.configuration.AgoraConfig;
import com.annular.filmhook.model.AgoraWebModel;
import com.annular.filmhook.service.AgoraTokenService;
import com.annular.filmhook.util.Utility;

import io.agora.media.RtcTokenBuilder;
import io.agora.rtm.RtmTokenBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgoraTokenSrvcImpl implements AgoraTokenService {

    private static final Logger logger = LoggerFactory.getLogger(AgoraTokenSrvcImpl.class);

    @Autowired
    AgoraConfig agoraConfig;

    @Override
    public String getAgoraRTCToken(AgoraWebModel agoraWebModel) {
        try {
            RtcTokenBuilder rtcTokenBuilder = new RtcTokenBuilder();

            String channelName = Utility.isNullOrBlankWithTrim(agoraWebModel.getChannelName()) ? agoraConfig.getChannelName() : agoraWebModel.getChannelName();
            int expireTime = agoraWebModel.getExpirationTimeInSeconds() == 0  ? agoraConfig.getExpirationTimeInSeconds() : agoraWebModel.getExpirationTimeInSeconds();
            RtcTokenBuilder.Role role = this.getRtcTokenBuilderRole(agoraWebModel.getRole());
            int uid = agoraWebModel.getUserId() == 0 ? agoraConfig.getUid() : agoraWebModel.getUserId();
            if (channelName == null) return "Channel ID cannot be blank"; // check for null channelName
            int timestamp = (int) (System.currentTimeMillis() / 1000 + expireTime);

            return rtcTokenBuilder.buildTokenWithUid(
                    agoraConfig.getCallAppId(),
                    agoraConfig.getCallAppCertificate(),
                    channelName,
                    uid,
                    role,
                    timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private RtcTokenBuilder.Role getRtcTokenBuilderRole(int role) {
        RtcTokenBuilder.Role responseRole = null;
        if (role == 0) {
            responseRole = RtcTokenBuilder.Role.Role_Attendee;
        } else if (role == 1) {
            responseRole = RtcTokenBuilder.Role.Role_Publisher;
        } else if (role == 2) {
            responseRole = RtcTokenBuilder.Role.Role_Subscriber;
        }
        return responseRole;
    }

    @Override
    public String getAgoraRTMToken(AgoraWebModel agoraWebModel) {
        try {
            if (agoraWebModel.getUserId() == null) return "User ID cannot be blank";
            RtmTokenBuilder token = new RtmTokenBuilder();
            return token.buildToken(
                    agoraConfig.getChatAppId(),
                    agoraConfig.getChatAppCertificate(),
                    String.valueOf(agoraWebModel.getUserId()),
                    RtmTokenBuilder.Role.Rtm_User,
                    agoraConfig.getExpirationTimeInSeconds());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
