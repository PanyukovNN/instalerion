package com.panyukovnn.instalerion.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panyukovnn.common.model.request.LoadVideoPostsRequest;
import com.panyukovnn.common.model.request.UploadVideoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaSender {

    @Value("${kafka.loader.topic}")
    private String LOADER_TOPIC_NAME;

    @Value("${kafka.publisher.topic}")
    private String PUBLISHER_TOPIC_NAME;

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    public void loaderCustomerIdSend(LoadVideoPostsRequest request) {
        Map<String, Object> mapRequest = objectMapper.convertValue(request, Map.class);

        ListenableFuture<SendResult<String, Map<String, Object>>> future =
                kafkaTemplate.send(LOADER_TOPIC_NAME, mapRequest);

        future.addCallback(new MapCallback(mapRequest));
    }

    public void publisherUploadVideoSend(UploadVideoRequest request) {
        Map<String, Object> mapRequest = objectMapper.convertValue(request, Map.class);

        ListenableFuture<SendResult<String, Map<String, Object>>> future =
                kafkaTemplate.send(PUBLISHER_TOPIC_NAME, mapRequest);

        future.addCallback(new MapCallback(mapRequest));
    }
}
