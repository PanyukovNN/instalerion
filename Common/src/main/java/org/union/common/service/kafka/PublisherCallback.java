package org.union.common.service.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.union.common.Constants;

import java.util.Map;

@RequiredArgsConstructor
public class PublisherCallback implements ListenableFutureCallback<SendResult<String, Map<String, Object>>> {

    private final Logger logger = LoggerFactory.getLogger(PublisherCallback.class);

    private final Map<String, Object> map;

    @Override
    public void onFailure(Throwable ex) {
        logger.info(String.format(Constants.ERROR_WHILE_PUBLISHER_REQUEST_SENDING, map, ex.getMessage()));
    }

    @Override
    public void onSuccess(SendResult<String, Map<String, Object>> result) {
        logger.info(String.format(Constants.PUBLISHER_REQUEST_SUCCESSFULLY_SENT, map));
    }
}
