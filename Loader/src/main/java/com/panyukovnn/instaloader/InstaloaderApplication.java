package com.panyukovnn.instaloader;

import com.panyukovnn.instaloader.service.LoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InstaloaderApplication implements CommandLineRunner {

    @Autowired
    private LoaderService loaderService;

    public static void main(String[] args) {
        SpringApplication.run(InstaloaderApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        loaderService.load();
    }
}
