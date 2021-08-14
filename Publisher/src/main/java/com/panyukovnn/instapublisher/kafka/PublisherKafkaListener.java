package com.panyukovnn.instapublisher.kafka;

import com.panyukovnn.common.model.request.PublishPostRequest;
import com.panyukovnn.common.service.kafka.KafkaHelper;
import com.panyukovnn.instapublisher.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.panyukovnn.common.Constants.ERROR_WHILE_PUBLICATION;
import static com.panyukovnn.common.Constants.UPLOAD_POST_REQUEST_RECEIVED_MSG;

/**
 * Kafka publish post request listener
 */
@Service
@RequiredArgsConstructor
public class PublisherKafkaListener {

    private final Logger logger = LoggerFactory.getLogger(PublisherKafkaListener.class);

    private final KafkaHelper kafkaHelper;
    private final PublisherService publisherService;

    @KafkaListener(topics = "${kafka.publisher.topic}", groupId = "${kafka.group}")
    public void listenPublishPostRequest(String request) {
        try {
            PublishPostRequest uploadVideoRequest = kafkaHelper.deserialize(request, PublishPostRequest.class);

            logger.info(String.format(UPLOAD_POST_REQUEST_RECEIVED_MSG, uploadVideoRequest.getPostId()));

            publisherService.publish(uploadVideoRequest);
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_PUBLICATION, request), e);

            //TODO send error message to topic PUBLISHER_ERRORS
        }
    }
}
