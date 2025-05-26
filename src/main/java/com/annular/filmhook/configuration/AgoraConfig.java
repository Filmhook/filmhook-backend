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

    @Value("${annular.app.agora.appId}")
    private String appId;

    @Value("${annular.app.agora.appCertificate}")
    private String appCertificate;

    @Value("${annular.app.agora.expire.second}")
    private int expirationTimeInSeconds;

    @Value("${annular.app.agora.appKey}")
    private String appKey;

    @Value("${annular.app.agora.domain}")
    private String appDomain;


    // RTC - Voice & Video Call
    private String channelName;
    private int uid = 0; // By default 0
    private int role = 2; // By default subscriber

    // RTM - Message
    private String userId;
}
