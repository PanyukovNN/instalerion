package org.union.promoter.service;

import org.union.common.exception.RequestException;
import org.union.common.service.DateTimeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.union.common.Constants.TOO_OFTEN_REQUESTS_ERROR_MSG;

/**
 * Helper service for request processors
 */
@Service
@RequiredArgsConstructor
public class RequestHelper {

    private final DateTimeHelper dateTimeHelper;

    @Value("${min.request.period.minutes}")
    private int minRequestPeriod;

    /**
     * Checks too often requests
     *
     * @param lastRequestDateTime date time of last request
     */
    public void checkOftenRequests(LocalDateTime lastRequestDateTime) {
        if (lastRequestDateTime != null) {
            int minutesDiff = dateTimeHelper.minuteFromNow(lastRequestDateTime);

            if (minRequestPeriod > minutesDiff) {
                throw new RequestException(TOO_OFTEN_REQUESTS_ERROR_MSG);
            }
        }
    }
}
