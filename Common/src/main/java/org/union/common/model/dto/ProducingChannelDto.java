package org.union.common.model.dto;


import org.union.common.model.ConsumingChannel;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.PublicationType;
import org.union.common.service.DateTimeHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.union.common.Constants.PROXY_SERVER_ADDRESS_FORMAT;

@Getter
@Setter
@NoArgsConstructor
public class ProducingChannelDto {

    private String id;
    private String login;
    private String password;
    private List<String> consumingChannelNames = new ArrayList<>();
    private Map<String, String> publicationTimeMap = new HashMap<>();
    private String lastLoadingDateTime;
    private Map<String, Integer> publishingPeriodMap = new HashMap<>();
    private String customerId;
    private String proxyServer;

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
        if (producingChannel.getPublicationTimeMap() != null) {
            for (Map.Entry<PublicationType, LocalDateTime> entry : producingChannel.getPublicationTimeMap().entrySet()) {
                this.publicationTimeMap.put(entry.getKey().name(), DateTimeHelper.FRONT_DATE_TIME.format(entry.getValue()));
            }
        }
        if (producingChannel.getLastLoadingDateTime() != null) {
            this.lastLoadingDateTime = DateTimeHelper.FRONT_DATE_TIME.format(producingChannel.getLastLoadingDateTime());
        }

        if (producingChannel.getPublishingPeriodMap() != null) {
            for (Map.Entry<PublicationType, Integer> entry : producingChannel.getPublishingPeriodMap().entrySet()) {
                this.publishingPeriodMap.put(entry.getKey().name(), entry.getValue());
            }
        }

        if (producingChannel.getCustomer() != null) {
            this.customerId = producingChannel.getCustomer().getId();
        }

        if (producingChannel.getProxyServer() != null) {
            this.proxyServer = String.format(PROXY_SERVER_ADDRESS_FORMAT,
                    producingChannel.getProxyServer().getIp(),
                    producingChannel.getProxyServer().getPort());
        }
    }
}
