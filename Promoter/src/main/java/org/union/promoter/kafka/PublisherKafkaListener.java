package org.union.promoter.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.union.common.model.request.PublishPostRequest;
import org.union.common.service.kafka.KafkaHelper;
import org.union.promoter.PromoterProperties;
import org.union.promoter.requestprocessor.PublisherRequestProcessor;
import org.union.promoter.service.RequestHelper;

import static org.union.common.Constants.ERROR_WHILE_PUBLICATION;
import static org.union.common.Constants.UPLOAD_POST_REQUEST_RECEIVED_MSG;

/**
 * Kafka listener for PUBLISHER topic
 */
@Service
@RequiredArgsConstructor
public class PublisherKafkaListener {

    @Value("${kafka.publisher.topic}")
    private String topicName;

    private final Logger logger = LoggerFactory.getLogger(PublisherKafkaListener.class);

    private final KafkaHelper kafkaHelper;
    private final RequestHelper requestHelper;
    private final PublisherRequestProcessor publisherRequestProcessor;

    @KafkaListener(topics = "${kafka.publisher.topic}", groupId = "${kafka.group}")
    public void listenPublisher(String request) {
        if (!PromoterProperties.publishingEnabled) {
            logger.info("Publisher disabled.");

            return;
        }

        try {
            requestHelper.checkOftenRequests(topicName);

            PublishPostRequest uploadVideoRequest = kafkaHelper.deserialize(request, PublishPostRequest.class);

            logger.info(String.format(UPLOAD_POST_REQUEST_RECEIVED_MSG, uploadVideoRequest.getPostId()));

            publisherRequestProcessor.publish(uploadVideoRequest);
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_PUBLICATION, request), e);
        }
    }
}
