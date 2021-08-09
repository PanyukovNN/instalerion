package com.panyukovnn.instapublisher.controller;

import com.panyukovnn.common.model.request.UploadVideoRequest;
import com.panyukovnn.instapublisher.service.PublisherService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PublisherKafkaListener {

    private final PublisherService publisherService;

    @KafkaListener(topics = "${kafka.publisher.topic}", groupId = "${kafka.group}")
    public void listenInstalerion(UploadVideoRequest request) throws NotFoundException, IOException {
        publisherService.uploadVideo(request.getConsumerId(), request.getVideoPost());
    }
}
