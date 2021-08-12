package com.panyukovnn.instapublisher.kafka;

import com.panyukovnn.common.model.request.PublishPostRequest;
import com.panyukovnn.common.service.kafka.KafkaHelper;
import com.panyukovnn.instapublisher.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.panyukovnn.common.Constants.ERROR_WHILE_PUBLICATION;
import static com.panyukovnn.common.Constants.UPLOAD_POST_REQUEST_RECEIVED_MSG;

/**
 * Kafka publish post request listener
 */
@Service
@RequiredArgsConstructor
public class PublisherKafkaListener {

    private final KafkaHelper kafkaHelper;
    private final PublisherService publisherService;

    @KafkaListener(topics = "${kafka.publisher.topic}", groupId = "${kafka.group}")
    public void listenPublishPostRequest(String request) {
        try {
            PublishPostRequest uploadVideoRequest = kafkaHelper.deserialize(request, PublishPostRequest.class);

            System.out.println(String.format(UPLOAD_POST_REQUEST_RECEIVED_MSG, uploadVideoRequest.getPostId()));

            publisherService.publish(uploadVideoRequest);
        } catch (Exception e) {
            System.out.println(String.format(ERROR_WHILE_PUBLICATION, request));

            //TODO send error message to topic PUBLISHER_ERRORS

            e.printStackTrace();
        }
    }
}
