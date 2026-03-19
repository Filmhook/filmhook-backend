package com.annular.filmhook.service.impl;

import com.annular.filmhook.configuration.AgoraConfig;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.webmodel.AgoraWebModel;
import com.annular.filmhook.service.AgoraTokenService;
import com.annular.filmhook.util.Utility;

import io.agora.chat.ChatTokenBuilder2;
import io.agora.media.RtcTokenBuilder;
import io.agora.media.RtcTokenBuilder2;
import io.agora.rtm.RtmTokenBuilder;
import io.agora.rtm.RtmTokenBuilder2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AgoraTokenSrvcImpl implements AgoraTokenService {

    @Autowired
    private AgoraConfig config;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AgoraTokenSrvcImpl.class);


    /* ---------------------------------------------------
     * RTC TOKEN (AUDIO / VIDEO CALL)
     * --------------------------------------------------- */
    @Override
    public String getRTCToken(AgoraWebModel model) {
        try {

            if (model.getChannelName() == null || model.getChannelName().isEmpty())
                return "Channel name cannot be blank";

            int uid = model.getUserId();
            if (uid <= 0) return "Invalid userId";

            int expire = model.getExpirationTimeInSeconds() == 0
                    ? config.getExpireSeconds()
                    : model.getExpirationTimeInSeconds();

            int expireTs = (int) (System.currentTimeMillis() / 1000 + expire);

            RtcTokenBuilder2 builder = new RtcTokenBuilder2();

            return builder.buildTokenWithUid(
                    config.getAppId(),
                    config.getAppCertificate(),
                    model.getChannelName(),
                    uid,
                    RtcTokenBuilder2.Role.ROLE_SUBSCRIBER,   // ✔ RTC uses roles
                    expireTs,
                    expireTs
            );

        } catch (Exception e) {
            logger.error("RTC token failed: {}", e.getMessage());
            return null;
        }
    }


    /* ---------------------------------------------------
     * RTM TOKEN (Real-time Messaging)
     * --------------------------------------------------- */
    @Override
    public String getRTMToken(AgoraWebModel model) {
        try {
            if (model.getUserId() == null)
                return "User ID cannot be blank";

            RtmTokenBuilder2 builder = new RtmTokenBuilder2();

            int expireTs = (int) (System.currentTimeMillis() / 1000 + config.getExpireSeconds());

            return builder.buildToken(
                    config.getAppId(),
                    config.getAppCertificate(),
                    String.valueOf(model.getUserId()),
                    expireTs
            );

        } catch (Exception e) {
            logger.error("RTM token error: {}", e.getMessage());
            return null;
        }
    }


    /* ---------------------------------------------------
     * CHAT TOKEN (Agora Chat)
     * --------------------------------------------------- */
    @Override
    public String getChatToken(AgoraWebModel model) {
        try {
            if (model.getUserId() == null)
                return "User ID cannot be blank";

            Optional<User> opt = userRepository.findById(model.getUserId());
            if (opt.isEmpty()) return "Invalid user";

            User user = opt.get();

            ChatTokenBuilder2 builder = new ChatTokenBuilder2();

            int expireTs = (int) (System.currentTimeMillis() / 1000 + config.getExpireSeconds());

            String token = builder.buildUserToken(
                    config.getAppId(),
                    config.getAppCertificate(),
                    String.valueOf(user.getUserId()),
                    expireTs
            );

            user.setTempToken(token);
            userRepository.save(user);

            return token;

        } catch (Exception e) {
            logger.error("Chat token failed: {}", e.getMessage());
            return null;
        }
    }
}
