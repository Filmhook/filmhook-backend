package com.annular.filmhook.configuration;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AgoraConfig {

	@Value("${annular.app.agora.appId}")
    private String appId;

    @Value("${annular.app.agora.appCertificate}")
    private String appCertificate;

    @Value("${annular.app.agora.expire.second}")
    private Integer expireSeconds;


    @Value("${annular.app.agora.domain}")
    private String domain;
}
