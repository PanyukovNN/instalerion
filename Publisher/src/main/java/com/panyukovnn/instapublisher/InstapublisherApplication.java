package com.panyukovnn.instapublisher;

import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.repository.CustomerRepository;
import com.panyukovnn.instapublisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"com.panyukovnn.instapublisher", "com.panyukovnn.common"})
@EnableMongoRepositories(basePackageClasses = {CustomerRepository.class})
public class InstapublisherApplication implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PublisherService publisherService;

    public static void main(String[] args) {
        SpringApplication.run(InstapublisherApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
//        String customerId = "6110322f8f21ee113e916f85";
//
//        Customer customer = customerRepository.findById(customerId).get();
//
//        publisherService.uploadVideo(customerId, customer.getConsumeChannels().get(0).getVideoPosts().get(0));
    }
}
