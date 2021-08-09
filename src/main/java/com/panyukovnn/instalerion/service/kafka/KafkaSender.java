package com.panyukovnn.instalerion.service.kafka;

import com.panyukovnn.common.model.request.UploadVideoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
public class KafkaSender {

    @Value("${kafka.loader.topic}")
    private String LOADER_TOPIC_NAME;

    @Value("${kafka.publisher.topic}")
    private String PUBLISHER_TOPIC_NAME;

    private final KafkaTemplate<String, String> kafkaCustomerIdTemplate;
    private final KafkaTemplate<String, UploadVideoRequest> kafkaUploadVideoTemplate;

    public void loaderCustomerIdSend(String customerId) {
        ListenableFuture<SendResult<String, String>> future = kafkaCustomerIdTemplate.send(LOADER_TOPIC_NAME, customerId);

        future.addCallback(new TextCallback(customerId));
    }

    public void publisherUploadVideoSend(UploadVideoRequest request) {
        ListenableFuture<SendResult<String, UploadVideoRequest>> future =
                kafkaUploadVideoTemplate.send(PUBLISHER_TOPIC_NAME, request);

        future.addCallback(new UploadVideoCallback(request));
    }
}
