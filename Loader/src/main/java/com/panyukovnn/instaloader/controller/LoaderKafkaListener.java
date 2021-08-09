package com.panyukovnn.instaloader.controller;

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
    public void listenInstalerion(String customerId) throws InterruptedException, ExecutionException, NotFoundException, IOException {
        loaderService.loadVideoPosts(customerId);
    }
}
