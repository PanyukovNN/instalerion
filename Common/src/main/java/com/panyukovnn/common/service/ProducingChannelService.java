package com.panyukovnn.common.service;

import com.panyukovnn.common.exception.NotFoundException;
import com.panyukovnn.common.model.ConsumingChannel;
import com.panyukovnn.common.model.ProducingChannel;
import com.panyukovnn.common.repository.ProducingChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.panyukovnn.common.Constants.CUSTOMER_NOT_FOUND_ERROR_MSG;

/**
 * Producing channels service
 */
@Service
@RequiredArgsConstructor
public class ProducingChannelService {

    private final EncryptionUtil encryptionUtil;
    private final ConsumingChannelService consumingChannelService;
    private final ProducingChannelRepository producingChannelRepository;

    @Transactional
    public void save(String login, String password, List<ConsumingChannel> consumingChannels) {
        ProducingChannel producingChannel = new ProducingChannel();
        producingChannel.setLogin(login);
        producingChannel.setPassword(encryptionUtil.getTextEncryptor().encrypt(password));

        List<ConsumingChannel> savedConsumingChannels = consumingChannelService.saveAll(consumingChannels);
        producingChannel.setConsumingChannels(savedConsumingChannels);

        producingChannelRepository.save(producingChannel);
    }

    @Transactional
    public void addConsumeChannels(String customerId, List<ConsumingChannel> consumingChannels) {
        ProducingChannel producingChannel = producingChannelRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND_ERROR_MSG));

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
}
