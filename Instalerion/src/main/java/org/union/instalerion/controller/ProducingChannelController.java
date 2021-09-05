package org.union.instalerion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.union.common.exception.NotFoundException;
import org.union.common.model.ConsumingChannel;
import org.union.common.model.Customer;
import org.union.common.model.ProducingChannel;
import org.union.common.model.dto.ProducingChannelDto;
import org.union.common.model.request.ChangeConsumingChannelsRequest;
import org.union.common.model.request.CreateUpdateProducingChannelRequest;
import org.union.common.service.ConsumingChannelService;
import org.union.common.service.CustomerService;
import org.union.common.service.ProducingChannelService;

import java.util.List;
import java.util.stream.Collectors;

import static org.union.common.Constants.CUSTOMER_NOT_FOUND_ERROR_MSG;
import static org.union.common.Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG;

@RestController
@RequiredArgsConstructor
@RequestMapping("/producing-channel")
public class ProducingChannelController {

    private final CustomerService customerService;
    private final ProducingChannelService producingChannelService;
    private final ConsumingChannelService consumingChannelService;

    @GetMapping("/all")
    public List<ProducingChannelDto> getAllProducingChannel() {
        return producingChannelService.findAll().stream()
                .map(ProducingChannelDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/create-update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String postCreateUpdateProducingChannel(@RequestBody CreateUpdateProducingChannelRequest request) {
        Customer customer = customerService.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException(String.format(CUSTOMER_NOT_FOUND_ERROR_MSG, request.getCustomerId())));

        List<ConsumingChannel> consumingChannels = request.getConsumingChannelNames().stream()
                .map(ConsumingChannel::new)
                .collect(Collectors.toList());

        producingChannelService.createOrUpdate(
                request.getProducingChannelId(),
                request.getLogin(),
                request.getPassword(),
                consumingChannels,
                request.getPostPublishingPeriod(),
                request.getStoryPublishingPeriod(),
                request.getSubject(),
                request.getHashtags(),
                customer);
        customerService.save(customer);

        return String.format("Producing channel \"%s\" successfully created.", request.getLogin());
    }

    @PostMapping("/remove")
    public String postRemoveProducingChannel(@RequestParam String producingChannelId) {
        ProducingChannel producingChannel = producingChannelService.findById(producingChannelId)
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannelId)));

        producingChannelService.remove(producingChannel);

        return String.format("Producing channel \"%s\" successfully removed", producingChannel.getLogin());
    }

    @PostMapping(value = "/consuming-channel/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String postAddConsumingChannels(@RequestBody ChangeConsumingChannelsRequest request) {
        ProducingChannel producingChannel = producingChannelService.findById(request.getProducingChannelId())
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, request.getProducingChannelId())));

        List<ConsumingChannel> consumingChannels = request.getConsumingChannelNames().stream()
                .filter(name -> producingChannel.getConsumingChannels().stream().noneMatch(pcc -> pcc.getName().equals(name)))
                .map(ConsumingChannel::new)
                .map(consumingChannelService::save)
                .collect(Collectors.toList());

        producingChannel.getConsumingChannels().addAll(consumingChannels);

        producingChannelService.save(producingChannel);

        return String.format("Consuming channels \"%s\" successfully added to \"%s\" producing channel",
                consumingChannels.stream()
                        .map(ConsumingChannel::getName)
                        .collect(Collectors.joining(",")),
                producingChannel.getLogin());
    }

    @PostMapping(value = "/consuming-channel/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String postRemoveConsumingChannels(@RequestBody ChangeConsumingChannelsRequest request) {
        ProducingChannel producingChannel = producingChannelService.findById(request.getProducingChannelId())
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, request.getProducingChannelId())));

        List<ConsumingChannel> consumingChannelsToRemove = producingChannel.getConsumingChannels().stream()
                .filter(cc -> request.getConsumingChannelNames().stream().anyMatch(name -> name.equals(cc.getName())))
                .collect(Collectors.toList());

        producingChannel.getConsumingChannels().removeAll(consumingChannelsToRemove);
        consumingChannelService.removeAll(consumingChannelsToRemove);
        producingChannelService.save(producingChannel);

        return String.format("Consuming channels \"%s\" successfully removed from \"%s\" producing channel",
                consumingChannelsToRemove.stream()
                        .map(ConsumingChannel::getName)
                        .collect(Collectors.joining(",")),
                producingChannel.getLogin());
    }
}
