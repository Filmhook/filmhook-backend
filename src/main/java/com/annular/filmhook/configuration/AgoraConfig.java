package com.annular.filmhook.configuration;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AgoraConfig {

    private static final Logger logger = LoggerFactory.getLogger(AgoraConfig.class);

    @Value("${annular.app.agora.call.appId}")
    private String callAppId;

    @Value("${annular.app.agora.call.appCertificate}")
    private String callAppCertificate;

    @Value("${annular.app.agora.chat.appId}")
    private String chatAppId;

    @Value("${annular.app.agora.chat.appCertificate}")
    private String chatAppCertificate;

    // RTC - Voice & Video Call
    private String channelName;
    private int uid = 0; // By default 0
    private int expirationTimeInSeconds = 3600; // By default 3600
    private int role = 2; // By default subscriber

    // RTM - Message
    private String userId;
}
