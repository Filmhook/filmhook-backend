package com.annular.filmhook.util;

import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class Utility {

    public static String getSiteUrl(HttpServletRequest httpServletRequest) {
        String siteUrl = httpServletRequest.getRequestURL().toString();
        return siteUrl.replace(httpServletRequest.getServletPath(), "");
    }

    public static boolean isNullOrBlankWithTrim(String value) {
        return value == null || value.trim().equals("null") || value.trim().isEmpty();
    }

    public static boolean isNullOrEmptyList(List<?> value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNullOrEmptySet(Set<?> value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNullOrEmptyMap(Map<?, ?> value) {
        return value == null || value.isEmpty();
    }
}
