package com.panyukovnn.common.service;

import com.panyukovnn.common.model.ConsumingChannel;
import com.panyukovnn.common.repository.ConsumingChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.panyukovnn.common.Constants.NULL_CONSUMING_CHANNEL_ERROR_MSG;

/**
 * Consuming channels service
 */
@Service
@RequiredArgsConstructor
public class ConsumingChannelService {

    private final ConsumingChannelRepository consumingChannelRepository;

    public ConsumingChannel findByName(String name) {
        return consumingChannelRepository.findByName(name)
                .orElse(new ConsumingChannel());
    }

    public ConsumingChannel save(ConsumingChannel consumingChannel) {
        if (consumingChannel == null) {
            throw new IllegalArgumentException(NULL_CONSUMING_CHANNEL_ERROR_MSG);
        }

        if (!consumingChannel.isFromDb()) {
            ConsumingChannel dbConsumingChannel = findByName(consumingChannel.getName());

            if (dbConsumingChannel.isFromDb()) {
                consumingChannel.setId(dbConsumingChannel.getId());
            }
        }

        return consumingChannelRepository.save(consumingChannel);
    }

    public List<ConsumingChannel> saveAll(List<ConsumingChannel> consumingChannels) {
        return consumingChannels.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    public List<ConsumingChannel> findAll() {
        return consumingChannelRepository.findAll();
    }
}
