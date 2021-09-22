package org.union.promoter.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.union.common.model.request.LoadingRequest;
import org.union.common.service.kafka.KafkaHelper;
import org.union.promoter.PromoterProperties;
import org.union.promoter.kafka.requestaspect.ListenerChecker;
import org.union.promoter.requestprocessor.LoaderRequestProcessor;
import org.union.promoter.service.RequestHelper;

import static org.union.common.Constants.*;

/**
 * Kafka listener for LOADER topic
 */
@Service
@RequiredArgsConstructor
public class LoaderKafkaListener implements Listener {

    private final Logger logger = LoggerFactory.getLogger(LoaderKafkaListener.class);

    @Getter
    @Value("${kafka.loader.topic}")
    private String topicName;

    private final KafkaHelper kafkaHelper;
    private final RequestHelper requestHelper;
    private final LoaderRequestProcessor loaderRequestProcessor;

    @ListenerChecker
    @KafkaListener(topics = "${kafka.loader.topic}", groupId = "${kafka.group}")
    public void listenLoader(String rawRequest) throws Exception {
        if (!PromoterProperties.loadingEnabled) {
            logger.info(LOADER_DISABLED_MSG);

            return;
        }

        LoadingRequest request = kafkaHelper.deserialize(rawRequest, LoadingRequest.class);
        requestHelper.validateLoaderRequest(request);

        logger.info(String.format(LOADING_REQUEST_RECEIVED_MSG, request));

        loaderRequestProcessor.processLoadingRequest(request);
    }
}
