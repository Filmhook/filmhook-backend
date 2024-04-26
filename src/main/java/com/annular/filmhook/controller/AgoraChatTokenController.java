package com.annular.filmhook.controller;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.UserRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.agora.chat.ChatTokenBuilder2;

@RestController
@CrossOrigin
public class AgoraChatTokenController {

    @Value("${appid}")
    private String appid;

    @Value("${appcert}")
    private String appcert;

    @Value("${expire.second}")
    private int expire;

    @Value("${appkey}")
    private String appkey;

    @Value("${domain}")
    private String domain;

    private final RestTemplate restTemplate = new RestTemplate();

    // Cache to store tokens
    private Cache<String, String> agoraChatAppTokenCache;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        agoraChatAppTokenCache = CacheBuilder.newBuilder()
                .maximumSize(100) // Max tokens to store
                .expireAfterWrite(expire, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Gets a token with app privileges for a specific user.
     *
     * @param userId The ID of the user for whom the token is generated
     * @return app privileges token
     */
    @GetMapping("/chat/app/token")
    public String getAppToken(String userId) {
        if (!StringUtils.hasText(appid) || !StringUtils.hasText(appcert)) {
            return "appid or appcert is not empty";
        }

        if (!StringUtils.hasText(userId)) {
            return "userId cannot be empty";
        }

        return getAgoraAppToken(userId);
    }

    /**
     * Generate a token for a specific user.
     *
     * @param userId The ID of the user for whom the token is generated
     * @return Agora app token
     */
    private String getAgoraAppToken(String userId) {
        String token = agoraChatAppTokenCache.getIfPresent(userId);
        if (token == null) {
            // Use agora App Id and App Cert to generate an Agora app token.
            ChatTokenBuilder2 builder = new ChatTokenBuilder2();
            token = builder.buildAppToken(appid, appcert, expire);
            agoraChatAppTokenCache.put(userId, token);
            // Save the token to the user table
            saveTokenToUserTable(userId, token);
        }
        return token;
    }

    /**
     * Save the token to the user table.
     *
     * @param userId The ID of the user
     * @param token The Agora app token
     */
    private void saveTokenToUserTable(String userId, String token) {
    	  User user = userRepository.findById(Integer.valueOf(userId)).orElse(null);
        if (user != null) {
            user.setTempToken(token);
            userRepository.save(user);
        }
    }
}
