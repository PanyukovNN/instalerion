package com.panyukovnn.instalerion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoaderRequestService extends AbstractRequestService {

    @Value("${loader.hostname}")
    private String loaderHostname;

    @Override
    public String getHostname() {
        return loaderHostname;
    }
}
