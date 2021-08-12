package com.panyukovnn.instalerion.service.scheduler;

import com.panyukovnn.common.model.request.LoadVideoPostsRequest;
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
        String customerId = "6110322f8f21ee113e916f85";
//        String customerId = "61142789176ee50133a93609";

        LoadVideoPostsRequest request = new LoadVideoPostsRequest(customerId);

        kafkaSender.loaderCustomerIdSend(request);
    }
}
