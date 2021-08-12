package com.panyukovnn.instaloader.kafka;

import com.panyukovnn.common.model.request.LoadVideoPostsRequest;
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

    private final LoaderService loaderService;

    @KafkaListener(topics = "${kafka.loader.topic}", groupId = "${kafka.group}")
    public void listenInstalerion(String request) {
        try {
            LoadVideoPostsRequest loadVideoPostsRequest = KafkaHelper.deserialize(request, LoadVideoPostsRequest.class);

            System.out.println(String.format(LOAD_POSTS_REQUEST_RECEIVED_MSG, loadVideoPostsRequest.getConsumerId()));

            loaderService.loadVideoPosts(loadVideoPostsRequest.getConsumerId());
        } catch (Exception e) {
            System.out.println(String.format(ERROR_WHILE_LOADING, request));

            e.printStackTrace();
        }

    }
}
