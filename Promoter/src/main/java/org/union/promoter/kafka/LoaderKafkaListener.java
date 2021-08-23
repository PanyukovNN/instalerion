package org.union.promoter.kafka;

import org.union.common.model.request.LoadPostsRequest;
import org.union.common.service.kafka.KafkaHelper;
import org.union.promoter.service.requestprocessor.LoaderRequestProcessor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static org.union.common.Constants.ERROR_WHILE_LOADING;
import static org.union.common.Constants.LOAD_POSTS_REQUEST_RECEIVED_MSG;

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
//            LoadPostsRequest loadPostsRequest = kafkaHelper.deserialize(request, LoadPostsRequest.class);
//
//            logger.info(String.format(LOAD_POSTS_REQUEST_RECEIVED_MSG, loadPostsRequest.getConsumerId()));
//
//            loaderRequestProcessor.load(loadPostsRequest.getConsumerId());
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_LOADING, request), e);

            //TODO send error message to topic PUBLISHER_ERRORS
        }
    }
}
