package com.panyukovnn.instalerion.service.scheduler;

import com.panyukovnn.common.model.request.LoadPostsRequest;
import com.panyukovnn.instalerion.service.kafka.KafkaSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoaderScheduler {

    private final KafkaSender kafkaSender;

    @Scheduled(fixedRateString = "${loader.scheduler.fixed.rate.mills}")
    public void scheduleLoaderKafkaSend() {
        String customerId = "6115880fe732f957a46fb24e";

        LoadPostsRequest request = new LoadPostsRequest(customerId);

        kafkaSender.sendLoadPosts(request);
    }
}
