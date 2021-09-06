package org.union.instalerion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.Post;
import org.union.common.model.post.PostRating;
import org.union.common.model.post.PublicationType;
import org.union.common.service.InstaService;
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
    InstaService instaService;

    public static void main(String[] args) {
        SpringApplication.run(InstalerionApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        ProducingChannel pc1 = new ProducingChannel();
        pc1.setId("1");
        pc1.setLogin("1");
        pc1.setPassword("1");

        ProducingChannel pc2 = new ProducingChannel();
        pc2.setId("2");
        pc2.setLogin("2");
        pc2.setPassword("2");

        ProducingChannel pc3 = new ProducingChannel();
        pc3.setId("3");
        pc3.setLogin("3");
        pc3.setPassword("3");

        InstaClient client1 = instaService.getClient(pc1);
        System.out.println(client1);

        InstaClient client2 = instaService.getClient(pc2);
        System.out.println(client2);

        InstaClient client3 = instaService.getClient(pc3);
        System.out.println(client3);
    }
}
