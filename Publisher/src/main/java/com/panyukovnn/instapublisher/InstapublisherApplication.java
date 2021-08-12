package com.panyukovnn.instapublisher;

import com.panyukovnn.common.repository.ProducingChannelRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"com.panyukovnn.instapublisher", "com.panyukovnn.common"})
@EnableMongoRepositories(basePackageClasses = {ProducingChannelRepository.class})
public class InstapublisherApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstapublisherApplication.class);
    }
}
