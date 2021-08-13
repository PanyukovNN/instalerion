package com.panyukovnn.common.service;

import com.panyukovnn.common.exception.NotFoundException;
import com.panyukovnn.common.model.ConsumingChannel;
import com.panyukovnn.common.model.ProducingChannel;
import com.panyukovnn.common.repository.ProducingChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.panyukovnn.common.Constants.PUBLICATION_CHANNEL_NOT_FOUND_ERROR_MSG;

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
    public void create(String login, String password, List<ConsumingChannel> consumingChannels) {
        ProducingChannel producingChannel = new ProducingChannel();
        producingChannel.setLogin(login);
        producingChannel.setPassword(encryptionUtil.getTextEncryptor().encrypt(password));

        List<ConsumingChannel> savedConsumingChannels = consumingChannelService.saveAll(consumingChannels);
        producingChannel.setConsumingChannels(savedConsumingChannels);

        producingChannelRepository.save(producingChannel);
    }

    @Transactional
    public void addConsumeChannels(String producingChannelId, List<ConsumingChannel> consumingChannels) {
        ProducingChannel producingChannel = producingChannelRepository.findById(producingChannelId)
                .orElseThrow(() -> new NotFoundException(PUBLICATION_CHANNEL_NOT_FOUND_ERROR_MSG));

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
}
