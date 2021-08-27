package org.union.instalerion.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.union.common.model.Customer;
import org.union.common.model.ProducingChannel;
import org.union.common.service.CustomerService;
import org.union.common.service.DateTimeHelper;
import org.union.common.service.ProducingChannelService;
import org.union.instalerion.kafka.LoaderKafkaSender;
import org.union.instalerion.kafka.PublisherKafkaSender;

import java.util.List;

import static org.union.common.Constants.*;

/**
 * Main processor
 */
@Service
@RequiredArgsConstructor
public class InstalerionService {

    private final Logger logger = LoggerFactory.getLogger(InstalerionService.class);

    private final DateTimeHelper dateTimeHelper;
    private final CustomerService customerService;
    private final LoaderKafkaSender loaderKafkaSender;
    private final PublisherKafkaSender publisherKafkaSender;
    private final ProducingChannelService producingChannelService;

    @Scheduled(fixedRateString = "${processor.scheduler.fixed.rate.mills}")
    public void schedule() {
        // skip night time
//        if (dateTimeHelper.isNight()) {
//            logger.info(WORKING_ON_PAUSE_IN_NIGHT_MSG);
//
//            return;
//        }

        List<Customer> customers = customerService.findAll();

        // TODO выводить сообщение если нет пользователей

        for (Customer customer : customers) {
            // TODO выводить сообщение какой пользователь взят в работу

            List<ProducingChannel> producingChannels = producingChannelService.findByConsumer(customer);

            // TODO выводить сообщение если нет каналов публикации

            for (ProducingChannel producingChannel: producingChannels) {
                if (!producingChannel.isEnabled()) {
                    logger.info(String.format(PRODUCING_CHANNEL_DISABLED_MSG, producingChannel.getId()));

                    continue;
                }

                if (producingChannel.getId() == null) {
                    logger.info(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannel.getId()));

                    continue;
                }

//                if (producingChannelService.isLoadingTime(producingChannel)) {
                    loaderKafkaSender.send(producingChannel.getId());
//                }

//                if (producingChannelService.isPublishingTime(producingChannel)) {
                    publisherKafkaSender.send(producingChannel.getId());
//                }
            }
        }
    }
}
