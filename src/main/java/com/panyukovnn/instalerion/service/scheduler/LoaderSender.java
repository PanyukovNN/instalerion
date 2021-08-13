package com.panyukovnn.instalerion.service.scheduler;

import com.panyukovnn.common.model.request.LoadPostsRequest;
import com.panyukovnn.instalerion.service.kafka.KafkaSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoaderSender {

    private final KafkaSender kafkaSender;

    public void scheduleLoaderKafkaSend(String customerId) {
        LoadPostsRequest request = new LoadPostsRequest(customerId);

        kafkaSender.sendLoadPosts(request);
    }
}
