package com.panyukovnn.instalerion;

import com.panyukovnn.common.repository.CustomerRepository;
import com.panyukovnn.common.repository.ProducingChannelRepository;
import com.panyukovnn.common.service.EncryptionUtil;
import com.panyukovnn.common.service.ProducingChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableMongoRepositories(basePackageClasses = {ProducingChannelRepository.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"com.panyukovnn.instalerion", "com.panyukovnn.common"})
public class InstalerionApplication implements CommandLineRunner {

    @Autowired
    private ProducingChannelService producingChannelService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    public static void main(String[] args) {
        SpringApplication.run(InstalerionApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
