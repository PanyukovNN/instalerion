package org.union.instalerion.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.union.common.model.post.Post;
import org.union.common.model.request.PublishPostRequest;
import org.union.common.service.PostService;
import org.union.common.service.kafka.KafkaHelper;
import org.union.common.service.kafka.PublisherCallback;

import java.util.Map;

import static org.union.common.Constants.POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG;

@Service
@RequiredArgsConstructor
public class PublisherKafkaSender implements KafkaSender {

    private final Logger logger = LoggerFactory.getLogger(PublisherKafkaSender.class);

    @Value("${kafka.publisher.topic}")
    private String PUBLISHER_TOPIC_NAME;

    private final KafkaHelper kafkaHelper;
    private final PostService postService;
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @Override
    public void send(String producingChannelId) {
        Post post = postService.findMostRated(producingChannelId)
                .orElse(null);

        if (post == null) {
            logger.info(POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG);

            return;
        }

        PublishPostRequest request = new PublishPostRequest(post.getId(), post.getMediaType());

        Map<String, Object> mapRequest = kafkaHelper.serialize(request);

        ListenableFuture<SendResult<String, Map<String, Object>>> future =
                kafkaTemplate.send(PUBLISHER_TOPIC_NAME, mapRequest);

        future.addCallback(new PublisherCallback(mapRequest));
    }
}