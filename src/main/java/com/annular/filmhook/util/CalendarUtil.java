package com.annular.filmhook.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Configuration
public class CalendarUtil {

    private static final Logger logger = LoggerFactory.getLogger(CalendarUtil.class);

    public final static String UI_DATE_FORMAT = "dd-MM-yyyy";
    public final static String MYSQL_DATE_FORMAT = "yyyy-MM-dd";
    public final static String YYYY_MM_DD = "yyyy-MM-dd";

    public final static DateTimeFormatter DD_MM_YYYY_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static String convertDateFormat(String inputFormat, String outputFormat, String inputDateTime) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
            final Date dateObj = sdf.parse(inputDateTime);
            return new SimpleDateFormat(outputFormat).format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Integer getAgeFromDate(String date, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDate birthDate = LocalDate.parse(date, dateTimeFormatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    public static long getDateDifferenceInDays(String inputDate, String timeZone) {
        logger.info("Date :- {}", inputDate);
        int year = Integer.parseInt(inputDate.substring(0, 4)); // yyyy
        int month = Integer.parseInt(inputDate.substring(5, 7)); // MM
        int day = Integer.parseInt(inputDate.substring(8, 10)); // dd

        LocalDate fromDate = LocalDate.of(year, month, day);
        LocalDate currentDate = LocalDate.now(ZoneId.of("IST", ZoneId.SHORT_IDS));
        logger.info("Days Diff :- {}", ChronoUnit.DAYS.between(fromDate, currentDate));
        return ChronoUnit.DAYS.between(fromDate, currentDate);
    }

    public static String getFormatedDateString(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static String getFormatedDateString(LocalDate date, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return date.format(dateTimeFormatter);
    }

    public static String calculateElapsedTime(LocalDateTime createdOn) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdOn, now);

        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + " s";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + (minutes == 1 ? " m" : " mts");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + (hours == 1 ? " h" : " hrs");
        } else if (seconds < 604800) {
            long days = seconds / 86400;
            return days + (days == 1 ? " d" : " days");
        } else {
            long weeks = seconds / 604800;
            return weeks + (weeks == 1 ? " w" : " weeks");
        }
    }

    public static LocalDate getNextDate(String date) {
        logger.debug("Input Date to get next day's date -> {}", date);

        LocalDate localDate = LocalDate.parse(date, DD_MM_YYYY_DATETIME_FORMATTER);
        logger.debug("Input date as LocalDate -> {}", localDate);

        LocalDate outputDate = localDate.plusDays(1);
        logger.debug("Next da LocalDate -> {}", outputDate);

        return outputDate;
    }
}
