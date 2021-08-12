package com.panyukovnn.instalerion.service.kafka;

import com.panyukovnn.common.model.request.LoadPostsRequest;
import com.panyukovnn.common.model.request.PublishPostRequest;
import com.panyukovnn.common.service.kafka.KafkaHelper;
import com.panyukovnn.common.service.kafka.MapCallback;
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

    private final KafkaHelper kafkaHelper;
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    /**
     * Send load posts request to kafka LOADER topic
     *
     * @param request load posts request
     */
    public void sendLoadPosts(LoadPostsRequest request) {
        Map<String, Object> mapRequest = kafkaHelper.serialize(request);

        ListenableFuture<SendResult<String, Map<String, Object>>> future =
                kafkaTemplate.send(LOADER_TOPIC_NAME, mapRequest);

        future.addCallback(new MapCallback(mapRequest));
    }

    /**
     * Send publish post request to kafka PUBLISHER topic
     *
     * @param request publish post request
     */
    public void sendPublishPost(PublishPostRequest request) {
        Map<String, Object> mapRequest = kafkaHelper.serialize(request);

        ListenableFuture<SendResult<String, Map<String, Object>>> future =
                kafkaTemplate.send(PUBLISHER_TOPIC_NAME, mapRequest);

        future.addCallback(new MapCallback(mapRequest));
    }
}
