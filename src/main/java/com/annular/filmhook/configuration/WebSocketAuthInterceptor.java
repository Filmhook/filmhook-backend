package com.annular.filmhook.configuration;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.annular.filmhook.model.UserSession;
import com.annular.filmhook.repository.UserSessionRepository;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

    	if (request instanceof org.springframework.http.server.ServletServerHttpRequest) {

    	    org.springframework.http.server.ServletServerHttpRequest servletRequest =
    	            (org.springframework.http.server.ServletServerHttpRequest) request;

    	    String sessionToken =
    	            servletRequest.getServletRequest()
    	                    .getHeader("sessionToken");

    	    if (sessionToken == null) {
    	    	sessionToken =
    	    	servletRequest.getServletRequest().getParameter("sessionToken");
    	    	}
    	    UserSession session =
    	            userSessionRepository.findBySessionToken(sessionToken);

    	    if (session != null && session.getIsActive()) {

    	        attributes.put("sessionToken", sessionToken);
    	        attributes.put("userId", session.getUserId());

    	        return true;
    	    }
    	}


        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }
}
