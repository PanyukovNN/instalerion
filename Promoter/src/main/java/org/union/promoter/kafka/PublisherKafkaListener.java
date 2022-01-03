package org.union.promoter.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.union.common.model.request.PublishingRequest;
import org.union.common.service.kafka.KafkaHelper;
import org.union.promoter.PromoterProperties;
import org.union.promoter.kafka.requestaspect.ListenerChecker;
import org.union.promoter.requestprocessor.PublisherRequestProcessor;
import org.union.promoter.service.RequestHelper;

import static org.union.common.Constants.*;

/**
 * Kafka listener for PUBLISHER topic
 */
@Service
@RequiredArgsConstructor
public class PublisherKafkaListener implements Listener {

    @Getter
    @Value("${kafka.publisher.topic}")
    public String topicName;

    private final Logger logger = LoggerFactory.getLogger(PublisherKafkaListener.class);

    private final KafkaHelper kafkaHelper;
    private final RequestHelper requestHelper;
    private final PublisherRequestProcessor publisherRequestProcessor;

    @ListenerChecker
    @KafkaListener(topics = "${kafka.publisher.topic}", groupId = "${kafka.group}")
    public void listenPublisher(String rawRequest) throws JsonProcessingException {
        if (!PromoterProperties.publishingEnabled) {
            logger.info(PUBLISHER_DISABLED_MSG);

            return;
        }

        requestHelper.isOftenRequests(topicName);

        PublishingRequest request = kafkaHelper.deserialize(rawRequest, PublishingRequest.class);
        requestHelper.validatePublisherRequest(request);

        logger.info(String.format(PUBLISHING_REQUEST_RECEIVED_MSG, request));

        publisherRequestProcessor.processPublishRequest(request);
    }
}
