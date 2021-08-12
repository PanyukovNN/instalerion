package com.panyukovnn.instapublisher.kafka;

import com.panyukovnn.common.model.request.UploadVideoRequest;
import com.panyukovnn.common.service.kafka.KafkaHelper;
import com.panyukovnn.instapublisher.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.panyukovnn.common.Constants.ERROR_WHILE_PUBLICATION;
import static com.panyukovnn.common.Constants.UPLOAD_POST_REQUEST_RECEIVED_MSG;

@Service
@RequiredArgsConstructor
public class PublisherKafkaListener {

    private final PublisherService publisherService;

    @KafkaListener(topics = "${kafka.publisher.topic}", groupId = "${kafka.group}")
    public void listenInstalerion(String request) {
        try {
            UploadVideoRequest uploadVideoRequest = KafkaHelper.deserialize(request, UploadVideoRequest.class);

            System.out.println(String.format(UPLOAD_POST_REQUEST_RECEIVED_MSG, uploadVideoRequest.getVideoPostId()));

            publisherService.uploadVideo(uploadVideoRequest);
        } catch (Exception e) {
            System.out.println(String.format(ERROR_WHILE_PUBLICATION, request));

            e.printStackTrace();
        }
    }
}
