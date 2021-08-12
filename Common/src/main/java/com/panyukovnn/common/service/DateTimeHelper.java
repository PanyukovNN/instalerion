package com.panyukovnn.common.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Util class to work with date time
 */
@Service
public class DateTimeHelper {

    public int minuteFromNow(LocalDateTime dateTime) {
        return (int) dateTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
    }
}
