package com.panyukovnn.instalerion.service.kafka;

import com.panyukovnn.common.model.post.Post;
import com.panyukovnn.common.model.request.PublishPostRequest;
import com.panyukovnn.common.repository.PostRepository;
import com.panyukovnn.common.service.kafka.KafkaHelper;
import com.panyukovnn.common.service.kafka.PublisherCallback;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;

import static com.panyukovnn.common.Constants.POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG;

@Service
@RequiredArgsConstructor
public class PublisherKafkaSender implements KafkaSender {

    @Value("${pubilshing.error.count.limit}")
    private int errorCountLimit;
    @Value("${kafka.publisher.topic}")
    private String PUBLISHER_TOPIC_NAME;

    private final KafkaHelper kafkaHelper;
    private final PostRepository postRepository;
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @Override
    public void send(String producingChannelId) {
        Post post = postRepository.findFirstByProducingChannelIdAndPublishDateTimeIsNullAndPublishingErrorCountLessThanEqual(producingChannelId, errorCountLimit);

        if (post == null) {
            System.out.println(POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG);

            return;
        }

        PublishPostRequest request = new PublishPostRequest(post.getId());

        Map<String, Object> mapRequest = kafkaHelper.serialize(request);

        ListenableFuture<SendResult<String, Map<String, Object>>> future =
                kafkaTemplate.send(PUBLISHER_TOPIC_NAME, mapRequest);

        future.addCallback(new PublisherCallback(mapRequest));
    }
}
