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
import org.union.common.service.PostService;
import org.union.common.service.ProducingChannelService;

import java.util.List;
import java.util.stream.Collectors;

import static org.union.common.Constants.CUSTOMER_NOT_FOUND_ERROR_MSG;
import static org.union.common.Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG;

@RestController
@RequiredArgsConstructor
public class InstalerionController {

    private final CustomerService customerService;
    private final ProducingChannelService producingChannelService;
    private final ConsumingChannelService consumingChannelService;

    @GetMapping("/user/all")
    public List<Customer> getAllUsers() {
        return customerService.findAll();
    }

    @GetMapping("/producing-channel/all")
    public List<ProducingChannelDto> getAllProducingChannel() {
        return producingChannelService.findAll().stream()
                .map(ProducingChannelDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/consuming-channel/all")
    public List<ConsumingChannel> getAllConsumingChannel() {
        return consumingChannelService.findAll();
    }

    @PostMapping("/user/create")
    public String postCreateUser(@RequestParam String username,
                                 @RequestParam String password) {
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(password);

        customerService.save(customer);

        return String.format("User \"%s\" successfully saved.", username);
    }

    @PostMapping("/user/remove")
    public String postRemoveCustomer(@RequestParam String userId) {
        Customer customer = customerService.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(CUSTOMER_NOT_FOUND_ERROR_MSG, userId)));

        customerService.remove(customer);

        return String.format("User \"%s\" successfully removed", customer.getUsername());
    }

    @PostMapping(value = "/producing-channel/create-update", consumes = MediaType.APPLICATION_JSON_VALUE)
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
                request.getPostingPeriod(),
                customer);
        customerService.save(customer);

        return String.format("Producing channel \"%s\" successfully created.", request.getLogin());
    }

    @PostMapping("/producing-channel/remove")
    public String postRemoveProducingChannel(@RequestParam String producingChannelId) {
        ProducingChannel producingChannel = producingChannelService.findById(producingChannelId)
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannelId)));

        producingChannelService.remove(producingChannel);

        return String.format("Producing channel \"%s\" successfully removed", producingChannel.getLogin());
    }

    @PostMapping(value = "/producing-channel/consuming-channel/add", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/producing-channel/consuming-channel/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
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
