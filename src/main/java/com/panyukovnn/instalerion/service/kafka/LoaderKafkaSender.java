package com.panyukovnn.instalerion.service.kafka;

import com.panyukovnn.common.model.request.LoadPostsRequest;
import com.panyukovnn.common.service.kafka.KafkaHelper;
import com.panyukovnn.common.service.kafka.LoaderCallback;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoaderKafkaSender implements KafkaSender {

    @Value("${kafka.loader.topic}")
    private String LOADER_TOPIC_NAME;

    private final KafkaHelper kafkaHelper;
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @Override
    public void send(String producingChannelId) {
        LoadPostsRequest request = new LoadPostsRequest(producingChannelId);

        Map<String, Object> mapRequest = kafkaHelper.serialize(request);

        ListenableFuture<SendResult<String, Map<String, Object>>> future =
                kafkaTemplate.send(LOADER_TOPIC_NAME, mapRequest);

        future.addCallback(new LoaderCallback(mapRequest));
    }
}
