package com.annular.filmhook.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.annular.filmhook.service.impl.UserDetailsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	 String path = request.getServletPath();
    	    if (path.equals("/user/login") || path.equals("/admin/login")) {
    	        filterChain.doFilter(request, response);
    	        return;
    	    }
    	try {
            String jwt = parseJwt(request);
            logger.info("JWT from request :- {}", jwt);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                logger.info("JWT available...");

                // Old one
                // String username = jwtUtils.getUserNameFromJwtToken(jwt);
                // UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // New one Username and usertype based login
                String userName = jwtUtils.getDataFromJwtToken(jwt, "userName");
                String userType = jwtUtils.getDataFromJwtToken(jwt, "userType");
                StringBuilder userNameWithUserType = new StringBuilder().append(userName).append("^").append(userType);
                logger.info("Username with UserType : {}", userNameWithUserType);

                UserDetails userDetails = userDetailsService.loadUserByUsername(userNameWithUserType.toString());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                logger.info("JWT not available...");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication...", e);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }
}