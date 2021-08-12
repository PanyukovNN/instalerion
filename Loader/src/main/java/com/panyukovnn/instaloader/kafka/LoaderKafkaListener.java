package com.panyukovnn.instaloader.kafka;

import com.panyukovnn.common.model.request.LoadPostsRequest;
import com.panyukovnn.common.service.kafka.KafkaHelper;
import com.panyukovnn.instaloader.service.LoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.panyukovnn.common.Constants.ERROR_WHILE_LOADING;
import static com.panyukovnn.common.Constants.LOAD_POSTS_REQUEST_RECEIVED_MSG;

@Service
@RequiredArgsConstructor
public class LoaderKafkaListener {

    private final KafkaHelper kafkaHelper;
    private final LoaderService loaderService;

    @KafkaListener(topics = "${kafka.loader.topic}", groupId = "${kafka.group}")
    public void listenInstalerion(String request) {
        try {
            LoadPostsRequest loadPostsRequest = kafkaHelper.deserialize(request, LoadPostsRequest.class);

            System.out.println(String.format(LOAD_POSTS_REQUEST_RECEIVED_MSG, loadPostsRequest.getConsumerId()));

            loaderService.load(loadPostsRequest.getConsumerId());
        } catch (Exception e) {
            System.out.println(String.format(ERROR_WHILE_LOADING, request));

            //TODO send error message to topic PUBLISHER_ERRORS

            e.printStackTrace();
        }
    }
}
