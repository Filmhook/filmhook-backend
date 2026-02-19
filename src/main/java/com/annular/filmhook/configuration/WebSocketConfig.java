package com.annular.filmhook.configuration;

import java.util.Collections;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.annular.filmhook.model.UserSession;
import com.annular.filmhook.repository.UserSessionRepository;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private UserSessionRepository userSessionRepository;
    private static final Logger log =
            LoggerFactory.getLogger(WebSocketConfig.class);
    // ✅ WebSocket Endpoint
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*");
    }

    // ✅ Message Broker Config
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

                    log.info("==== WS CONNECT ATTEMPT ====");

                    String sessionToken =
                            accessor.getFirstNativeHeader("sessionToken");

                    log.info("TOKEN RECEIVED: {}", sessionToken);

                    if (sessionToken == null || sessionToken.trim().isEmpty()) {
                        log.warn("❌ Missing sessionToken");
                        return null;
                    }

                    UserSession session =
                            userSessionRepository.findBySessionToken(sessionToken);

                    if (session == null) {
                        log.warn("❌ Session not found in DB");
                        return null;
                    }

                    if (!Boolean.TRUE.equals(session.getIsActive())) {
                        log.warn("❌ Session is not active");
                        return null;
                    }

                    log.info("✅ WS AUTH SUCCESS for user {}", session.getUserId());

                    accessor.setUser(
                            new UsernamePasswordAuthenticationToken(
                                    session.getUserId().toString(),
                                    null,
                                    Collections.emptyList()
                            )
                    );
                }

                return message;
            }
        });
    }
}
