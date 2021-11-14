package org.union.common.service;

import org.union.common.Constants;
import org.union.common.model.ConsumingChannel;
import org.union.common.repository.ConsumingChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
            throw new IllegalArgumentException(Constants.NULL_CONSUMING_CHANNEL_ERROR_MSG);
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

    public void remove(ConsumingChannel consumingChannel) {
        consumingChannelRepository.delete(consumingChannel);
    }

    public void removeAll(List<ConsumingChannel> consumingChannels) {
        consumingChannelRepository.deleteAll(consumingChannels);
    }
}
