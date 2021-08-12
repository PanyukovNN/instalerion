package com.panyukovnn.instaloader;

import com.panyukovnn.common.repository.CustomerRepository;
import com.panyukovnn.common.service.CustomerService;
import com.panyukovnn.instaloader.kafka.LoaderKafkaListener;
import com.panyukovnn.instaloader.service.LoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"com.panyukovnn.instaloader", "com.panyukovnn.common"})
@EnableMongoRepositories(basePackageClasses = {CustomerRepository.class})
public class InstaloaderApplication implements CommandLineRunner {

    @Autowired
    private LoaderService loaderService;

    @Autowired
    private LoaderKafkaListener kafkaConsumer;

    @Autowired
    private CustomerService customerService;

    public static void main(String[] args) {
        SpringApplication.run(InstaloaderApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
//        String customerId = "6110322f8f21ee113e916f85";
//
//        loaderService.loadVideoPosts(customerId);

//        kafkaConsumer.listenGroupFoo();
    }
}
