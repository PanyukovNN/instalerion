package com.panyukovnn.instaloader;

import com.panyukovnn.common.model.ConsumeChannel;
import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.repository.CustomerRepository;
import com.panyukovnn.instaloader.service.LoaderService;
import com.panyukovnn.instaloader.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"com.panyukovnn.instaloader", "com.panyukovnn.common"})
@EnableMongoRepositories(basePackageClasses = {CustomerRepository.class})
public class InstaloaderApplication implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoaderService loaderService;

    @Autowired
    private PublisherService publisherService;

    public static void main(String[] args) {
        SpringApplication.run(InstaloaderApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        String customerId = "6110322f8f21ee113e916f85";
//
//        loaderService.loadVideoPosts(customerId);

        Customer customer = customerRepository.findById(customerId).get();

        publisherService.uploadVideo(customerId, customer.getConsumeChannels().get(0).getVideoPosts().get(0));
    }
}
