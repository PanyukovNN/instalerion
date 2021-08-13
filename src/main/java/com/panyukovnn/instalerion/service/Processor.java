package com.panyukovnn.instalerion.service;

import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.model.ProducingChannel;
import com.panyukovnn.common.service.CustomerService;
import com.panyukovnn.common.service.ProducingChannelService;
import com.panyukovnn.instalerion.service.kafka.LoaderKafkaSender;
import com.panyukovnn.instalerion.service.kafka.PublisherKafkaSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.panyukovnn.common.Constants.PUBLICATION_CHANNEL_NOT_FOUND_ERROR_MSG;

/**
 * Main processor
 */
@Service
@RequiredArgsConstructor
public class Processor {

    private final CustomerService customerService;
    private final LoaderKafkaSender loaderKafkaSender;
    private final PublisherKafkaSender publisherKafkaSender;
    private final ProducingChannelService producingChannelService;

    @Scheduled(fixedRateString = "${processor.scheduler.fixed.rate.mills}")
    public void schedule() {
        // skip night time
//        if (dateTimeHelper.isNight()) {
//            System.out.println(WORKING_ON_PAUSE_IN_NIGHT_MSG);
//
//            return;
//        }

        List<Customer> customers = customerService.findAll();

        for (Customer customer : customers) {
            Set<String> producingChannelIds = customer.getProducingChannelIds();

            for (String producingChannelId : producingChannelIds) {
                ProducingChannel producingChannel = producingChannelService.findById(producingChannelId)
                        .orElse(new ProducingChannel());

                if (producingChannel.getId() == null) {
                    System.out.println(String.format(PUBLICATION_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannelId));

                    continue;
                }

                if (producingChannelService.isLoadingTime(producingChannel)) {
                    loaderKafkaSender.send(producingChannel.getId());
                }

                if (producingChannelService.isPublishingTime(producingChannel)) {
                    publisherKafkaSender.send(producingChannel.getId());
                }
            }
        }
    }
}
