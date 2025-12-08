package com.annular.filmhook.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LocalDateListConverter implements AttributeConverter<List<LocalDate>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(List<LocalDate> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public List<LocalDate> convertToEntityAttribute(String joined) {
        if (joined == null || joined.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(joined.split(DELIMITER))
                     .map(LocalDate::parse)
                     .collect(Collectors.toList());
    }
}
