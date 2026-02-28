package com.annular.filmhook.util;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.annular.filmhook.model.GroupCallMember;
import com.annular.filmhook.repository.GroupCallMemberRepository;
import com.annular.filmhook.webmodel.WebSocketMessage;

@Component
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private GroupCallMemberRepository groupCallMemberRepo;
    
       

    public void notifyUser(Integer userId, String eventType, Object payload) {
    	messagingTemplate.convertAndSendToUser(
    	        String.valueOf(userId),
    	        "/queue/call",
    	        new WebSocketMessage(eventType, payload)
    	);
    }
    
    public void notifyChatUser(Integer userId, String eventType, Object payload) {
        messagingTemplate.convertAndSendToUser(
            String.valueOf(userId),
            "/queue/chat",
            new WebSocketMessage(eventType, payload)
        );
    }
    
    /* ---------------------------------------------------------
     * Send WebSocket event to ALL members in a group call
     * --------------------------------------------------------- */
    public void notifyGroup(Integer groupCallId, String eventType, Map<String, Object> payload) {

        List<GroupCallMember> members =
                groupCallMemberRepo.findByGroupCallId(groupCallId);

        if (members == null || members.isEmpty()) {
            System.out.println("notifyGroup → No members for groupCallId=" + groupCallId);
            return;
        }

        for (GroupCallMember m : members) {
            notifyUser(m.getUserId(), eventType, payload);
        }
    }

    /* ---------------------------------------------------------
     * OPTIONAL: notifyGroup but skip the sender
     * --------------------------------------------------------- */
    public void notifyGroupExcept(Integer groupCallId, Integer skipUserId,
                                  String eventType, Map<String, Object> payload) {

        List<GroupCallMember> members =
                groupCallMemberRepo.findByGroupCallId(groupCallId);

        if (members == null || members.isEmpty()) return;

        for (GroupCallMember m : members) {
            if (!m.getUserId().equals(skipUserId)) {
                notifyUser(m.getUserId(), eventType, payload);
            }
        }
    }
}
