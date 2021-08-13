package com.panyukovnn.common.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;

import static com.panyukovnn.common.Constants.ERROR_WHILE_PUBLISHER_REQUEST_SENDING;
import static com.panyukovnn.common.Constants.PUBLISHER_REQUEST_SUCCESSFULLY_SENT;

@RequiredArgsConstructor
public class PublisherCallback implements ListenableFutureCallback<SendResult<String, Map<String, Object>>> {

    private final Map<String, Object> map;

    @Override
    public void onFailure(Throwable ex) {
        System.out.println(String.format(ERROR_WHILE_PUBLISHER_REQUEST_SENDING, map, ex.getMessage()));
    }

    @Override
    public void onSuccess(SendResult<String, Map<String, Object>> result) {
        System.out.println(String.format(PUBLISHER_REQUEST_SUCCESSFULLY_SENT, map));
    }
}
