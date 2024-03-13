package com.annular.filmhook;

import javax.servlet.http.HttpServletRequest;

public class Utility {
	public static String getSiteUrl(HttpServletRequest httpServletRequest) {
		String siteUrl = httpServletRequest.getRequestURL().toString();
		return siteUrl.replace(httpServletRequest.getServletPath(), "");
	}

}
