package org.union.promoter.kafka;

import org.union.common.model.request.LoadPostsRequest;
import org.union.common.service.UseContext;
import org.union.common.service.kafka.KafkaHelper;
import org.union.promoter.PromoterProperties;
import org.union.promoter.requestprocessor.LoaderRequestProcessor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static org.union.common.Constants.*;

/**
 * Kafka listener for LOADER topic
 */
@Service
@RequiredArgsConstructor
public class LoaderKafkaListener {

    private final Logger logger = LoggerFactory.getLogger(LoaderKafkaListener.class);

    private final KafkaHelper kafkaHelper;
    private final LoaderRequestProcessor loaderRequestProcessor;

    @KafkaListener(topics = "${kafka.loader.topic}", groupId = "${kafka.group}")
    public void listenLoader(String request) {
        try {
            if (!PromoterProperties.loadingEnabled) {
                logger.info(LOADER_DISABLED_MSG);

                return;
            }

            LoadPostsRequest loadPostsRequest = kafkaHelper.deserialize(request, LoadPostsRequest.class);

            String producingChannelId = loadPostsRequest.getProducingChannelId();

            if (producingChannelId == null) {
                throw new IllegalArgumentException(PRODUCING_CHANNEL_NULL_ID_ERROR_MSG);
            }

            logger.info(String.format(LOAD_POSTS_REQUEST_RECEIVED_MSG, producingChannelId));

            try {
                UseContext.setInUse(producingChannelId);
                loaderRequestProcessor.load(producingChannelId);
            } finally {
                UseContext.release(producingChannelId);
            }
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_LOADING, request), e);
        }
    }
}
