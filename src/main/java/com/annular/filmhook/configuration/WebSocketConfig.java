package com.annular.filmhook.configuration;

import java.util.Collections;

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

    // ✅ WebSocket Endpoint
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("https://www.filmhookapps.com");
    }

    // ✅ Message Broker Config
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    // ✅ SECURITY — Token Validation Happens Here
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String sessionToken =
                            accessor.getFirstNativeHeader("sessionToken");

                    if (sessionToken == null) {
                        throw new IllegalArgumentException("Missing sessionToken");
                    }

                    UserSession session =
                            userSessionRepository.findBySessionToken(sessionToken);

                    if (session == null || !session.getIsActive()) {
                        throw new IllegalArgumentException("Invalid sessionToken");
                    }

                    // 🔐 Bind user to Principal
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
