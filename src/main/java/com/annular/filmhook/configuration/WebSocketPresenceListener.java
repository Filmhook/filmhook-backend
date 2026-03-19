package com.annular.filmhook.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.annular.filmhook.util.WebSocketService;



@Component
public class WebSocketPresenceListener {

    @Autowired
    private WebSocketService webSocketService;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = accessor.getFirstNativeHeader("userId");

        if (userId == null) return;

        Map<String,Object> data = new HashMap<>();
        data.put("userId", Integer.parseInt(userId));
        data.put("status", "ONLINE");

        webSocketService.notifyChatUser(
                Integer.parseInt(userId),
                "ONLINE_STATUS",
                data
        );

        System.out.println("User Online: " + userId);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = accessor.getFirstNativeHeader("userId");

        if (userId == null) return;

        Map<String,Object> data = new HashMap<>();
        data.put("userId", Integer.parseInt(userId));
        data.put("status", "OFFLINE");

        webSocketService.notifyChatUser(
                Integer.parseInt(userId),
                "ONLINE_STATUS",
                data
        );

        System.out.println("User Offline: " + userId);
    }
}