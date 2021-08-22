package org.union.common.service;

import org.union.common.exception.NotFoundException;
import org.union.common.model.ConsumingChannel;
import org.union.common.model.Customer;
import org.union.common.model.ProducingChannel;
import org.union.common.repository.ProducingChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.union.common.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Producing channels service
 */
@Service
@RequiredArgsConstructor
public class ProducingChannelService {

    private final EncryptionUtil encryptionUtil;
    private final DateTimeHelper dateTimeHelper;
    private final ConsumingChannelService consumingChannelService;
    private final ProducingChannelRepository producingChannelRepository;

    public Optional<ProducingChannel> findById(String producingChannelId) {
        return producingChannelRepository.findById(producingChannelId);
    }

    public ProducingChannel save(ProducingChannel producingChannel) {
        return producingChannelRepository.save(producingChannel);
    }

    @Transactional
    public void createOrUpdate(String producingChannelId,
                               String login,
                               String password,
                               List<ConsumingChannel> consumingChannels,
                               int postingPeriod,
                               Customer customer) {
        ProducingChannel producingChannel = new ProducingChannel();
        if (producingChannelId != null) {
            ProducingChannel producingChannelFromDb = producingChannelRepository.findById(producingChannelId)
                    .orElseThrow(() -> new NotFoundException(String.format(Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannelId)));
            producingChannel.setId(producingChannelFromDb.getId());
        }

        producingChannel.setLogin(login);
        producingChannel.setPassword(encryptionUtil.getTextEncryptor().encrypt(password));

        List<ConsumingChannel> savedConsumingChannels = consumingChannelService.saveAll(consumingChannels);
        producingChannel.setConsumingChannels(savedConsumingChannels);

        producingChannel.setPostingPeriod(postingPeriod);
        producingChannel.setCustomer(customer);

        producingChannelRepository.save(producingChannel);
    }

    @Transactional
    public void addConsumeChannels(String producingChannelId, List<ConsumingChannel> consumingChannels) {
        ProducingChannel producingChannel = producingChannelRepository.findById(producingChannelId)
                .orElseThrow(() -> new NotFoundException(String.format(Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannelId)));

        if (producingChannel.getConsumingChannels() == null) {
            producingChannel.setConsumingChannels(new ArrayList<>());
        }

        for (ConsumingChannel consumingChannel : consumingChannels) {
            boolean matchByName = producingChannel.getConsumingChannels().stream()
                    .anyMatch(cc -> cc.getName().equals(consumingChannel.getName()));

            if (!matchByName) {
                producingChannel.getConsumingChannels().add(consumingChannel);
            }
        }

        List<ConsumingChannel> savedConsumingChannels = consumingChannelService.saveAll(producingChannel.getConsumingChannels());
        producingChannel.setConsumingChannels(savedConsumingChannels);

        producingChannelRepository.save(producingChannel);
    }

    /**
     * Is it time to publish post
     *
     * @param producingChannel producing channel
     * @return is publishing time
     */
    public boolean isPublishingTime(ProducingChannel producingChannel) {
        int prosingPeriod = producingChannel.getPostingPeriod();

        LocalDateTime lastPostingDateTime = producingChannel.getLastPostingDateTime();

        // if first publication
        if (lastPostingDateTime == null) {
            return true;
        }

        int minutesFromLastPosting = dateTimeHelper.minuteFromNow(lastPostingDateTime);

        return minutesFromLastPosting >= prosingPeriod;
    }

    /**
     * Is it time to load posts
     *
     * @param producingChannel producing channel
     * @return is loading time
     */
    public boolean isLoadingTime(ProducingChannel producingChannel) {
        int prosingPeriod = producingChannel.getPostingPeriod() * 2 / 3;

        LocalDateTime lastLoadingDateTime = producingChannel.getLastLoadingDateTime();

        // if first loading
        if (lastLoadingDateTime == null) {
            return true;
        }

        int minutesFromLastLoading = dateTimeHelper.minuteFromNow(lastLoadingDateTime);

        return minutesFromLastLoading >= prosingPeriod;
    }

    public List<ProducingChannel> findAll() {
        return producingChannelRepository.findAll();
    }

    public void remove(ProducingChannel producingChannel) {
        producingChannelRepository.delete(producingChannel);
    }

    public List<ProducingChannel> findByConsumer(Customer customer) {
        return producingChannelRepository.findByCustomer(customer);
    }
}
