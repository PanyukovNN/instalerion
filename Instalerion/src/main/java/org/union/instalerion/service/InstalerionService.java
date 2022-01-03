package org.union.instalerion.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.union.common.model.Customer;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.PublicationType;
import org.union.common.model.request.LoadingRequest;
import org.union.common.model.request.PublishingRequest;
import org.union.common.service.CustomerService;
import org.union.common.service.DateTimeHelper;
import org.union.common.service.ProducingChannelService;
import org.union.common.service.loadingstrategy.LoadingStrategyType;
import org.union.common.service.publishingstrategy.PostSortingStrategyType;
import org.union.instalerion.kafka.LoaderKafkaSender;
import org.union.instalerion.kafka.PublisherKafkaSender;

import java.util.List;

import static org.union.common.Constants.*;
import static org.union.instalerion.InstalerionProperties.publishAtNight;

/**
 * Instalerion processor
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
        if (dateTimeHelper.isNight() && !publishAtNight) {
            logger.info(WORKING_ON_PAUSE_IN_NIGHT_MSG);

            return;
        }

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
            logger.info(PRODUCING_CHANNELS_NOT_FOUND_MSG, customer.getUsername());

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

        sendLoadingRequests(producingChannel);

        if (producingChannel.getLastLoadingDateTime() != null) {
            sendPublishingRequests(producingChannel);
        } else {
            logger.info(REQUEST_FOR_PUBLICATION_COULD_BE_SENT_BEFORE_LOADING_MSG);
        }
    }

    private void sendLoadingRequests(ProducingChannel producingChannel) {
        if (producingChannelService.isLoadingTime(producingChannel)) {
            LoadingStrategyType strategyType = LoadingStrategyType.INSTAGRAM_POSTS;

            LoadingRequest request = new LoadingRequest(producingChannel.getId(), strategyType, STANDARD_LOADING_VOLUME);

            loaderKafkaSender.send(request);
        }
    }

    private void sendPublishingRequests(ProducingChannel producingChannel) {
        if (producingChannelService.isPostPublishingTime(producingChannel)) {
            PublicationType publicationType = PublicationType.INSTAGRAM_POST;
            PostSortingStrategyType postSortingStrategyType = PostSortingStrategyType.MOST_RECENT;

            PublishingRequest request = new PublishingRequest(producingChannel.getId(),
                    publicationType, postSortingStrategyType);

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
            logger.info(PRODUCING_CHANNEL_DISABLED_MSG, producingChannel.getId());

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
