package org.union.promoter.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.union.common.model.request.PublishingRequest;
import org.union.common.service.kafka.KafkaHelper;
import org.union.promoter.PromoterProperties;
import org.union.promoter.requestprocessor.PublisherRequestProcessor;
import org.union.promoter.service.RequestHelper;

import static org.union.common.Constants.*;

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
    public void listenPublisher(String rawRequest) {
        if (!PromoterProperties.publishingEnabled) {
            logger.info(PUBLISHER_DISABLED_MSG);

            return;
        }

        try {
            requestHelper.checkOftenRequests(topicName);

            PublishingRequest request = kafkaHelper.deserialize(rawRequest, PublishingRequest.class);
            requestHelper.validatePublisherRequest(request);

            logger.info(String.format(PUBLISHING_REQUEST_RECEIVED_MSG, request.getProducingChannelId()));

            publisherRequestProcessor.processPublishRequest(request);

            requestHelper.requestFinished(topicName);
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_PUBLICATION, rawRequest), e);
        }
    }
}
