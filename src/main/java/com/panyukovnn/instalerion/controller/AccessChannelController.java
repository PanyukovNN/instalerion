package com.panyukovnn.instalerion.controller;

import com.panyukovnn.common.exception.NotFoundException;
import com.panyukovnn.common.model.ConsumingChannel;
import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.model.ProducingChannel;
import com.panyukovnn.common.model.dto.ProducingChannelDto;
import com.panyukovnn.common.model.post.Post;
import com.panyukovnn.common.model.request.ChangeConsumingChannelsRequest;
import com.panyukovnn.common.model.request.CreateUpdateProducingChannelRequest;
import com.panyukovnn.common.service.ConsumingChannelService;
import com.panyukovnn.common.service.CustomerService;
import com.panyukovnn.common.service.PostService;
import com.panyukovnn.common.service.ProducingChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.panyukovnn.common.Constants.CUSTOMER_NOT_FOUND_ERROR_MSG;
import static com.panyukovnn.common.Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG;

@RequiredArgsConstructor
@RestController("/access-channel")
public class AccessChannelController {

    private final PostService postService;
    private final CustomerService customerService;
    private final ProducingChannelService producingChannelService;

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
                request.getPostingPeriod(),
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
}
