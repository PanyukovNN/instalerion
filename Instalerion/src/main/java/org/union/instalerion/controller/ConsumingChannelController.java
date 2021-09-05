package org.union.instalerion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.union.common.model.ConsumingChannel;
import org.union.common.service.ConsumingChannelService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consuming-channel")
public class ConsumingChannelController {

    private final ConsumingChannelService consumingChannelService;

    @GetMapping("/all")
    public List<ConsumingChannel> getAllConsumingChannel() {
        return consumingChannelService.findAll();
    }
}
