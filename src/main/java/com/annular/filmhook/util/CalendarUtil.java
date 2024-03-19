package com.annular.filmhook.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Configuration
public class CalendarUtil {
    private static final Logger logger = LoggerFactory.getLogger(CalendarUtil.class);
    public final static String UI_DATE_FORMAT = "dd-MM-yyyy";
    public final static String MYSQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String YYYY_MM_DD = "yyyy-MM-dd";

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

    public Integer getAgeFromDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4)); // yyyy
        int month = Integer.parseInt(date.substring(5, 7)); // MM
        int day = Integer.parseInt(date.substring(8, 10)); // dd
        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    public static long getDateDifferenceInDays(String inputDate, String timeZone) {
        logger.info("Date :- " + inputDate);
        int year = Integer.parseInt(inputDate.substring(0, 4)); // yyyy
        int month = Integer.parseInt(inputDate.substring(5, 7)); // MM
        int day = Integer.parseInt(inputDate.substring(8, 10)); // dd

        LocalDate fromDate = LocalDate.of(year, month, day);
        LocalDate currentDate = LocalDate.now(ZoneId.of("IST", ZoneId.SHORT_IDS));
        logger.info("Days Diff :- " + ChronoUnit.DAYS.between(fromDate, currentDate));
        return ChronoUnit.DAYS.between(fromDate, currentDate);
    }

    public static String getFormatedDateString(Date date) {
        return new SimpleDateFormat(YYYY_MM_DD).format(date);
    }
}
