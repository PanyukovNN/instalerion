package com.panyukovnn.instalerion.service;

import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.model.ProducingChannel;
import com.panyukovnn.common.service.CustomerService;
import com.panyukovnn.common.service.DateTimeHelper;
import com.panyukovnn.common.service.ProducingChannelService;
import com.panyukovnn.instalerion.kafka.LoaderKafkaSender;
import com.panyukovnn.instalerion.kafka.PublisherKafkaSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.panyukovnn.common.Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG;
import static com.panyukovnn.common.Constants.WORKING_ON_PAUSE_IN_NIGHT_MSG;

/**
 * Main processor
 */
@Service
@RequiredArgsConstructor
public class Processor {

    private final Logger logger = LoggerFactory.getLogger(Processor.class);

    private final DateTimeHelper dateTimeHelper;
    private final CustomerService customerService;
    private final LoaderKafkaSender loaderKafkaSender;
    private final PublisherKafkaSender publisherKafkaSender;
    private final ProducingChannelService producingChannelService;

    @Scheduled(fixedRateString = "${processor.scheduler.fixed.rate.mills}")
    public void schedule() {
        // skip night time
        if (dateTimeHelper.isNight()) {
            logger.info(WORKING_ON_PAUSE_IN_NIGHT_MSG);

            return;
        }

        List<Customer> customers = customerService.findAll();

        for (Customer customer : customers) {
            List<ProducingChannel> producingChannels = producingChannelService.findByConsumer(customer);

            for (ProducingChannel producingChannel: producingChannels) {
                if (producingChannel.getId() == null) {
                    logger.info(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannel.getId()));

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
