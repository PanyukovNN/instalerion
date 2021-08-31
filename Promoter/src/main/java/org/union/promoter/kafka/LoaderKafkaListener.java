package org.union.promoter.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.union.common.model.request.LoadPostsRequest;
import org.union.common.service.kafka.KafkaHelper;
import org.union.common.service.loadingstrategy.LoadingStrategy;
import org.union.common.service.loadingstrategy.LoadingStrategyType;
import org.union.common.service.loadingstrategy.LoadingVolume;
import org.union.promoter.PromoterProperties;
import org.union.promoter.requestprocessor.LoaderRequestProcessor;
import org.union.promoter.service.RequestHelper;
import org.union.promoter.service.StrategyResolver;

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
            requestHelper.checkOftenRequests(topicName);

            LoadPostsRequest request = kafkaHelper.deserialize(rawRequest, LoadPostsRequest.class);
            requestHelper.validateLoaderRequest(request);

            logger.info(String.format(LOAD_POSTS_REQUEST_RECEIVED_MSG, request.getProducingChannelId()));

            loaderRequestProcessor.load(request);
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_LOADING, rawRequest), e);
        }
    }
}
