package com.panyukovnn.common.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Util class to work with date time
 */
@Service
public class DateTimeHelper {

    public static final DateTimeFormatter FRONT_DATE_TIME = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public int minuteFromNow(LocalDateTime dateTime) {
        return (int) dateTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
    }

    /**
     * Returns true between 23:00 and 9:00
     *
     * @return is night now
     */
    public boolean isNight() {
        LocalDateTime now = LocalDateTime.now();

        return now.getHour() > 22 || now.getHour() < 8;
    }
}
