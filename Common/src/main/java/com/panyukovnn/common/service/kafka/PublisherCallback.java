package com.panyukovnn.common.service.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;

import static com.panyukovnn.common.Constants.*;

@RequiredArgsConstructor
public class PublisherCallback implements ListenableFutureCallback<SendResult<String, Map<String, Object>>> {

    private final Logger logger = LoggerFactory.getLogger(PublisherCallback.class);

    private final Map<String, Object> map;

    @Override
    public void onFailure(Throwable ex) {
        logger.info(String.format(ERROR_WHILE_PUBLISHER_REQUEST_SENDING, map, ex.getMessage()));
    }

    @Override
    public void onSuccess(SendResult<String, Map<String, Object>> result) {
        logger.info(String.format(PUBLISHER_REQUEST_SUCCESSFULLY_SENT, map));
    }
}
