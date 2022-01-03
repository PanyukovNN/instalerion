package org.union.promoter.service;

import org.union.common.exception.TooOftenRequestException;
import org.union.common.model.request.LoadingRequest;
import org.union.common.model.request.PublishingRequest;
import org.union.common.service.DateTimeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.union.common.Constants.*;

/**
 * Helper service for request processors
 */
@Service
@RequiredArgsConstructor
public class RequestHelper {

    private final DateTimeHelper dateTimeHelper;

    @Value("${min.request.period.minutes}")
    private int minRequestPeriod;

    private final Map<String, LocalDateTime> topicRequestContext = new HashMap<>();

    /**
     * Checks too often requests
     */
    public void isOftenRequests(String topicName) {
        LocalDateTime lastRequestDateTime = topicRequestContext.get(topicName);

        if (lastRequestDateTime == null) {
            return;
        }

        int minutesDiff = dateTimeHelper.minuteFromNow(lastRequestDateTime);

        if (minRequestPeriod > minutesDiff) {
            throw new TooOftenRequestException();
        }
    }

    /**
     * Add record with request finish time
     *
     * @param topicName name of topic
     */
    public void requestFinished(String topicName) {
        topicRequestContext.put(topicName, dateTimeHelper.getCurrentDateTime());
    }

    /**
     * Validates {@link LoadingRequest}
     *
     * @param request request
     */
    public void validateLoaderRequest(LoadingRequest request) {
        if (request.getProducingChannelId() == null) {
            throw new IllegalArgumentException(PRODUCING_CHANNEL_NULL_ID_ERROR_MSG);
        }

        if (request.getStrategyType() == null) {
            throw new IllegalArgumentException(LOADING_STRATEGY_TYPE_NULL_ID_ERROR_MSG);
        }
    }

    /**
     * Validates {@link PublishingRequest}
     *
     * @param request request
     */
    public void validatePublisherRequest(PublishingRequest request) {
        if (request.getProducingChannelId() == null) {
            throw new IllegalArgumentException(PRODUCING_CHANNEL_NULL_ID_ERROR_MSG);
        }

        if (request.getPublicationType() == null) {
            throw new IllegalArgumentException(LOADING_STRATEGY_TYPE_NULL_ID_ERROR_MSG);
        }

        if (request.getPostSortingStrategyType() == null) {
            throw new IllegalArgumentException(POST_SORTING_STRATEGY_NULL_ERROR_MSG);
        }
    }
}
