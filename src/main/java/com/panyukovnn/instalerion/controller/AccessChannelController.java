package com.panyukovnn.instalerion.controller;

import com.panyukovnn.common.model.AccessChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/access-channel")
public class AccessChannelController {

    @GetMapping("/get")
    public String getAllProducingChannel() {
        return AccessChannel.login;
    }
}
