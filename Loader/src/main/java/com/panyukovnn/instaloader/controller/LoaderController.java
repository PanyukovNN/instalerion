package com.panyukovnn.instaloader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoaderController {

    @GetMapping("/")
    public String getLoader() {
        return "hello from loader";
    }
}
