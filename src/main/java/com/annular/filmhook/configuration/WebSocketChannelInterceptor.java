package com.annular.filmhook.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.*;
import org.springframework.stereotype.Component;

import com.annular.filmhook.model.UserSession;
import com.annular.filmhook.repository.UserSessionRepository;

@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String sessionToken = accessor.getFirstNativeHeader("sessionToken");

            if (sessionToken != null) {

                UserSession session =
                        userSessionRepository.findBySessionToken(sessionToken);

                if (session != null && session.getIsActive()) {

                    accessor.setUser(
                            new org.springframework.security.authentication
                                    .UsernamePasswordAuthenticationToken(
                                    session.getUserId().toString(),
                                    null,
                                    null
                            )
                    );
                }
            }
        }

        return message;
    }
}
