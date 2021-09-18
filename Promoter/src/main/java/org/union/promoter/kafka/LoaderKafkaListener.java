package org.union.promoter.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.union.common.exception.TooOftenRequestException;
import org.union.common.model.request.LoadingRequest;
import org.union.common.service.kafka.KafkaHelper;
import org.union.promoter.PromoterProperties;
import org.union.promoter.requestprocessor.LoaderRequestProcessor;
import org.union.promoter.service.RequestHelper;

import static org.union.common.Constants.*;

/**
 * Kafka listener for LOADER topic
 */
@Service
@RequiredArgsConstructor
public class LoaderKafkaListener {

    private final Logger logger = LoggerFactory.getLogger(LoaderKafkaListener.class);

    @Value("${kafka.loader.topic}")
    private String topicName;

    private final KafkaHelper kafkaHelper;
    private final RequestHelper requestHelper;
    private final LoaderRequestProcessor loaderRequestProcessor;

    @KafkaListener(topics = "${kafka.loader.topic}", groupId = "${kafka.group}")
    public void listenLoader(String rawRequest) {
        if (!PromoterProperties.loadingEnabled) {
            logger.info(LOADER_DISABLED_MSG);

            return;
        }

        try {
            requestHelper.isOftenRequests(topicName);

            LoadingRequest request = kafkaHelper.deserialize(rawRequest, LoadingRequest.class);
            requestHelper.validateLoaderRequest(request);

            logger.info(String.format(LOADING_REQUEST_RECEIVED_MSG, request));

            loaderRequestProcessor.processLoadingRequest(request);

            requestHelper.requestFinished(topicName);
        } catch (TooOftenRequestException e) {
            logger.info(String.format(TOO_OFTEN_REQUESTS_ERROR_MSG, topicName));
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_LOADING, rawRequest), e);
        }
    }
}
