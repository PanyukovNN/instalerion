package org.union.instalerion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.union.instalerion.InstalerionProperties;

@RestController
@RequestMapping("/properties")
public class PropertiesController {

    @GetMapping("/switch-night-publishing")
    public String switchNightPublication() {
        InstalerionProperties.publishAtNight = !InstalerionProperties.publishAtNight;

        return String.format("Publishing at night switched to \"%s\" state",
                InstalerionProperties.publishAtNight ? "ENABLED" : "DISABLED");
    }
}
