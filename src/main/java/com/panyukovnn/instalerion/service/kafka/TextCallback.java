package com.panyukovnn.instalerion.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

@RequiredArgsConstructor
public class TextCallback implements ListenableFutureCallback<SendResult<String, String>> {

    private final String text;

    @Override
    public void onFailure(Throwable ex) {
        System.out.println("Unable to send message=["
                + text + "] due to : " + ex.getMessage());
    }

    @Override
    public void onSuccess(SendResult<String, String> result) {
        System.out.println("Sent message=[" + text +
                "] with offset=[" + result.getRecordMetadata().offset() + "]");
    }
}
