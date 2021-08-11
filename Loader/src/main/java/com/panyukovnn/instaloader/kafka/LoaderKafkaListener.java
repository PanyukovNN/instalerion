package com.panyukovnn.instaloader.kafka;

import com.panyukovnn.common.model.request.LoadVideoPostsRequest;
import com.panyukovnn.common.service.kafka.KafkaHelper;
import com.panyukovnn.instaloader.service.LoaderService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class LoaderKafkaListener {

    private final LoaderService loaderService;

    @KafkaListener(topics = "${kafka.loader.topic}", groupId = "${kafka.group}")
    public void listenInstalerion(String request) throws IOException, InterruptedException, ExecutionException, NotFoundException {
        LoadVideoPostsRequest loadVideoPostsRequest = KafkaHelper.deserialize(request, LoadVideoPostsRequest.class);

        loaderService.loadVideoPosts(loadVideoPostsRequest.getConsumerId());
    }
}
