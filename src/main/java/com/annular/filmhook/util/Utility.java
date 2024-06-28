package com.annular.filmhook.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class Utility {

    private static final Logger logger = LoggerFactory.getLogger(Utility.class);

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

    public static boolean isNullOrZero(Integer value) {
        return value == null || value == 0;
    }

    public static boolean isNullObject(Object obj) {
        return obj == null;
    }

    public static double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new NumberFormatException("Empty or null string");
        }
        if (value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("Infinity")) {
            throw new NumberFormatException("Invalid double value: " + value);
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            // Log the value causing the exception for debugging
            logger.error("Invalid double value: {}", value);
            throw new NumberFormatException("Invalid double value: " + value);
        }
    }

    /* // Benisha's code to get Distance between two co-ordinates = not calculating properly. kept for reference
    public static double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int EARTH_RADIUS = 6371; // Radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                + Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }*/

    public static double calculateDistance(Double startLat, Double startLong, Double endLat, Double endLong) {
        final int EARTH_RADIUS = 6371; // Radius in kilometers

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public static double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}
