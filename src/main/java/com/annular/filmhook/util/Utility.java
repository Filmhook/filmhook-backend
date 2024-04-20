package com.annular.filmhook.util;

import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class Utility {

	public static String getSiteUrl(HttpServletRequest httpServletRequest) {
		String siteUrl = httpServletRequest.getRequestURL().toString();
		return siteUrl.replace(httpServletRequest.getServletPath(), "");
	}

	public static boolean isNullOrBlankWithTrim(String value){
		return value == null || value.trim().equals("null") || value.trim().isEmpty();
	}
}
