package com.panyukovnn.instalerion.service.kafka;

import com.panyukovnn.common.model.request.UploadVideoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

@RequiredArgsConstructor
public class UploadVideoCallback implements ListenableFutureCallback<SendResult<String, UploadVideoRequest>> {

    private final UploadVideoRequest request;

    @Override
    public void onFailure(Throwable ex) {
        System.out.println("Unable to send uploadVideoRequest=[consumerId="
                + request.getConsumerId() + ";videoPostCode=" + request.getVideoPost().getCode()
                + "] due to : " + ex.getMessage());
    }

    @Override
    public void onSuccess(SendResult<String, UploadVideoRequest> result) {
        System.out.println("Sent uploadVideoRequest=[consumerId="
                + request.getConsumerId() + ";videoPostCode=" + request.getVideoPost().getCode()
                + "] with offset=[" + result.getRecordMetadata().offset() + "]");
    }
}
