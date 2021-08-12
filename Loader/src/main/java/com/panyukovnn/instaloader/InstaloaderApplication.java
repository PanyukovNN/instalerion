package com.panyukovnn.instaloader;

import com.panyukovnn.common.repository.ProducingChannelRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"com.panyukovnn.instaloader", "com.panyukovnn.common"})
@EnableMongoRepositories(basePackageClasses = {ProducingChannelRepository.class})
public class InstaloaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstaloaderApplication.class);
    }
}
