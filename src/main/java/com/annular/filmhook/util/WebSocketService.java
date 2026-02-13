package com.annular.filmhook.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.annular.filmhook.webmodel.WebSocketMessage;

@Component
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyUser(Integer userId, String eventType, Object payload) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/call",
                new WebSocketMessage(eventType, payload)
        );
    }
}
