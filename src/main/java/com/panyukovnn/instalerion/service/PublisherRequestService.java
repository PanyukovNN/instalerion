package com.panyukovnn.instalerion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PublisherRequestService extends AbstractRequestService {

    @Value("${publisher.hostname}")
    private String publisherHostname;

    @Override
    public String getHostname() {
        return publisherHostname;
    }
}
