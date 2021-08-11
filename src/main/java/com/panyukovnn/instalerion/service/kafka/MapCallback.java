package com.panyukovnn.instalerion.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;

@RequiredArgsConstructor
public class MapCallback implements ListenableFutureCallback<SendResult<String, Map<String, Object>>> {

    private final Map<String, Object> map;

    @Override
    public void onFailure(Throwable ex) {
        System.out.println("Unable to send message=["
                + map + "] due to : " + ex.getMessage());
    }

    @Override
    public void onSuccess(SendResult<String, Map<String, Object>> result) {
        System.out.println("Sent message=[" + map +
                "] with offset=[" + result.getRecordMetadata().offset() + "]");
    }
}
