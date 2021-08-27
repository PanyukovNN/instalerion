package org.union.promoter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.union.promoter.PromoterProperties;

@RestController
public class PromoterController {

    @GetMapping("/loader-status")
    public String loaderStatus() {
        return "Loader is " + (PromoterProperties.loadingEnabled ? "on" : "off");
    }

    @GetMapping("/switch-loader")
    public String switchLoader() {
        PromoterProperties.loadingEnabled = !PromoterProperties.loadingEnabled;

        return loaderStatus();
    }

    @GetMapping("/publisher-status")
    public String publisherStatus() {
        return "Publisher is " + (PromoterProperties.publishingEnabled ? "on" : "off");
    }

    @GetMapping("/switch-publisher")
    public String switchPublisher() {
        PromoterProperties.publishingEnabled = !PromoterProperties.publishingEnabled;

        return publisherStatus();
    }
}
