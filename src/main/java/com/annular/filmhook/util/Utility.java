package com.annular.filmhook.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    /**
     *  Helper method to generate OTP
     * @param length
     * @return OTP
     */
    public static String generateOtp(int length) {
        Random random = new Random();
        return IntStream.range(0, length).mapToObj(i -> String.valueOf(random.nextInt(10))).collect(Collectors.joining());
    }
    
    
    public static String formatRelativeTime(Date viewedDate) {
        if (viewedDate == null) return "unknown";

        LocalDateTime viewedTime = viewedDate.toInstant()
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toLocalDateTime();

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        Duration duration = Duration.between(viewedTime, now);
        long minutes = duration.toMinutes();
        long hours = duration.toHours();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        if (minutes < 1) return "just now";
       // if (minutes < 60) return minutes + " minutes ago";
        if (viewedTime.toLocalDate().equals(now.toLocalDate()))
            return "Today, " + viewedTime.format(timeFormatter);
        if (viewedTime.toLocalDate().equals(now.toLocalDate().minusDays(1)))
            return "Yesterday at " + viewedTime.format(timeFormatter);

        return viewedTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

}
