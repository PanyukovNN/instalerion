package com.panyukovnn.common.model.dto;


import com.panyukovnn.common.model.ConsumingChannel;
import com.panyukovnn.common.model.ProducingChannel;
import com.panyukovnn.common.service.DateTimeHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ProducingChannelDto {

    private String id;
    private String login;
    private String password;
    private List<String> consumingChannelNames;
    private String lastPostingDateTime;
    private String lastLoadingDateTime;
    private int postingPeriod;
    private String customerId;

    public ProducingChannelDto(ProducingChannel producingChannel) {
        if (producingChannel == null) {
            return;
        }

        this.id = producingChannel.getId();
        this.login = producingChannel.getLogin();
        this.password = producingChannel.getPassword();
        this.consumingChannelNames = producingChannel.getConsumingChannels() != null
                ? producingChannel.getConsumingChannels().stream()
                .map(ConsumingChannel::getName)
                .collect(Collectors.toList())
                : Collections.emptyList();
        if (producingChannel.getLastPostingDateTime() != null) {
            this.lastPostingDateTime = DateTimeHelper.FRONT_DATE_TIME.format(producingChannel.getLastPostingDateTime());
        }
        if (producingChannel.getLastLoadingDateTime() != null) {
            this.lastLoadingDateTime = DateTimeHelper.FRONT_DATE_TIME.format(producingChannel.getLastLoadingDateTime());
        }
        this.postingPeriod = producingChannel.getPostingPeriod();

        if (producingChannel.getCustomer() != null) {
            this.customerId = producingChannel.getCustomer().getId();
        }}
}
