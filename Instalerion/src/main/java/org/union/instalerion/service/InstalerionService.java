package org.union.instalerion.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.union.common.model.Customer;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.Post;
import org.union.common.model.request.LoadingRequest;
import org.union.common.model.request.PublishingRequest;
import org.union.common.service.CustomerService;
import org.union.common.service.DateTimeHelper;
import org.union.common.service.PostService;
import org.union.common.service.ProducingChannelService;
import org.union.common.service.loadingstrategy.LoadingStrategyType;
import org.union.common.service.publishingstrategy.PublishingStrategyType;
import org.union.instalerion.kafka.LoaderKafkaSender;
import org.union.instalerion.kafka.PublisherKafkaSender;

import java.util.List;

import static org.union.common.Constants.*;

/**
 * Instalerion processor
 */
@Service
@RequiredArgsConstructor
public class InstalerionService {

    private final Logger logger = LoggerFactory.getLogger(InstalerionService.class);

    private final PostService postService;
    private final DateTimeHelper dateTimeHelper;
    private final CustomerService customerService;
    private final LoaderKafkaSender loaderKafkaSender;
    private final PublisherKafkaSender publisherKafkaSender;
    private final ProducingChannelService producingChannelService;

    @Scheduled(fixedRateString = "${processor.scheduler.fixed.rate.mills}")
    public void schedule() {
//        if (dateTimeHelper.isNight()) {
//            logger.info(WORKING_ON_PAUSE_IN_NIGHT_MSG);
//
//            return;
//        }

        List<Customer> customers = customerService.findAll();

        if (CollectionUtils.isEmpty(customers)) {
            logger.info(CUSTOMERS_NOT_FOUND_MSG);

            return;
        }

        customers.forEach(this::processCustomer);
    }

    /**
     * Process customer
     *
     * @param customer customer
     */
    private void processCustomer(Customer customer) {
        List<ProducingChannel> producingChannels = producingChannelService.findByConsumer(customer);

        if (CollectionUtils.isEmpty(producingChannels)) {
            logger.info(String.format(PRODUCING_CHANNELS_NOT_FOUND_MSG, customer.getUsername()));

            return;
        }

        producingChannels.forEach(this::processProducingChannel);
    }

    /**
     * Process producing channel
     *
     * @param producingChannel producing channel
     */
    private void processProducingChannel(ProducingChannel producingChannel) {
        if (producingChannelProcessingNotAllowed(producingChannel)) {
            return;
        }

        if (producingChannelService.isLoadingTime(producingChannel)) {
            LoadingStrategyType strategyType = LoadingStrategyType.INSTAGRAM_POSTS;

            LoadingRequest request = new LoadingRequest(producingChannel.getId(), strategyType, STANDARD_LOADING_VOLUME);

            loaderKafkaSender.send(request);
        }

        if (producingChannelService.isPublishingTime(producingChannel)) {
            PublishingStrategyType strategyType = PublishingStrategyType.INSTAGRAM_STORY;

            PublishingRequest request = new PublishingRequest(producingChannel.getId(), strategyType);

            publisherKafkaSender.send(request);
        }
    }

    /**
     * Check is processing allowed
     *
     * @param producingChannel producing channel
     * @return is processing allowed
     */
    private boolean producingChannelProcessingNotAllowed(ProducingChannel producingChannel) {
        if (producingChannel.getId() == null) {
            logger.info(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannel.getId()));

            return true;
        }

        if (!producingChannel.isEnabled()) {
            logger.info(String.format(PRODUCING_CHANNEL_DISABLED_MSG, producingChannel.getId()));

            return true;
        }

        if (producingChannelService.isBlocked(producingChannel)) {
            logger.info(String.format(PRODUCING_CHANNEL_TEMPORARY_BLOCKED_MSG,
                    producingChannel.getId(),
                    producingChannelService.getUnblockingFormattedDateTime(producingChannel)));

            return true;
        }

        return false;
    }
}
