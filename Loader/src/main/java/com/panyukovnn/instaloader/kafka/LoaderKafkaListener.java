package com.panyukovnn.instaloader.kafka;

import com.panyukovnn.common.model.request.LoadPostsRequest;
import com.panyukovnn.common.service.kafka.KafkaHelper;
import com.panyukovnn.instaloader.service.LoaderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.panyukovnn.common.Constants.ERROR_WHILE_LOADING;
import static com.panyukovnn.common.Constants.LOAD_POSTS_REQUEST_RECEIVED_MSG;

@Service
@RequiredArgsConstructor
public class LoaderKafkaListener {

    private final Logger logger = LoggerFactory.getLogger(LoaderKafkaListener.class);

    private final KafkaHelper kafkaHelper;
    private final LoaderService loaderService;

    @KafkaListener(topics = "${kafka.loader.topic}", groupId = "${kafka.group}")
    public void listenInstalerion(String request) {
        try {
            LoadPostsRequest loadPostsRequest = kafkaHelper.deserialize(request, LoadPostsRequest.class);

            logger.info(String.format(LOAD_POSTS_REQUEST_RECEIVED_MSG, loadPostsRequest.getConsumerId()));

            loaderService.load(loadPostsRequest.getConsumerId());
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_LOADING, request), e);

            //TODO send error message to topic PUBLISHER_ERRORS

            e.printStackTrace();
        }
    }
}
