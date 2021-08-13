package com.panyukovnn.instalerion;

import com.panyukovnn.common.model.ConsumingChannel;
import com.panyukovnn.common.repository.ProducingChannelRepository;
import com.panyukovnn.common.service.ProducingChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@EnableScheduling
@EnableMongoRepositories(basePackageClasses = {ProducingChannelRepository.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"com.panyukovnn.instalerion", "com.panyukovnn.common"})
public class InstalerionApplication implements CommandLineRunner {

    @Autowired
    private ProducingChannelService producingChannelService;

    public static void main(String[] args) {
        SpringApplication.run(InstalerionApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
