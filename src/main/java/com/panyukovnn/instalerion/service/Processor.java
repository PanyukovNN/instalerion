package com.panyukovnn.instalerion.service;

import com.panyukovnn.instalerion.service.scheduler.LoaderSender;
import com.panyukovnn.instalerion.service.scheduler.PublisherSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Processor {

    private final LoaderSender loaderSender;
    private final PublisherSender publisherSender;

    @Scheduled(fixedRateString = "${processor.scheduler.fixed.rate.mills}")
    public void schedule() {
        //        String customerId = "6115880fe732f957a46fb24e"; // insta_rus_love
        String customerId = "6116b1b307cd155b943bedb3"; // too_happy_bee


    }
}
