package com.annular.filmhook.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.annular.filmhook.service.impl.AdminDetailsServiceImpl;


public class AdminAuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AdminDetailsServiceImpl adminDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
    	String path = request.getServletPath();
    	if (path.equals("/admin/login")) {
    	    filterChain.doFilter(request, response);
    	    return;
    	}

        try {
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                String userType =
                        jwtUtils.getDataFromJwtToken(jwt, "userType");

                if (!"ADMIN".equals(userType)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String email =
                        jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails adminDetails =
                        adminDetailsService
                                .loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                adminDetails,
                                null,
                                adminDetails.getAuthorities());

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer "))
                ? header.substring(7)
                : null;
    }
}
