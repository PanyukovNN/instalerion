package org.union.instalerion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.Post;
import org.union.common.model.post.PostRating;
import org.union.common.model.post.PublicationType;
import org.union.common.service.PostService;
import org.union.common.service.ProducingChannelService;

import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@EnableMongoRepositories(basePackages = "org.union.common.repository")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"org.union.instalerion", "org.union.common"})
public class InstalerionApplication implements CommandLineRunner {

    @Autowired
    PostService postService;

    @Autowired
    ProducingChannelService producingChannelService;

    public static void main(String[] args) {
        SpringApplication.run(InstalerionApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
