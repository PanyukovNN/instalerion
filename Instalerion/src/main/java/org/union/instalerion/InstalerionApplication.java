package org.union.instalerion;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableMongoRepositories
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"org.union.instalerion", "org.union.common"})
public class InstalerionApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(InstalerionApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
