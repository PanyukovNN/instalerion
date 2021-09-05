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
//        Post post1 = new Post();
//        post1.setCode("1");
//        post1.getPublishedTimeByType().put(PublicationType.INSTAGRAM_POST, LocalDateTime.now());
//        post1.setProducingChannelId("6134bcec27411d5faf124a1c");
//        post1.setRating(new PostRating(1d));
//
//        Post post2 = new Post();
//        post2.setCode("2");
//        post2.getPublishedTimeByType().put(PublicationType.INSTAGRAM_STORY, LocalDateTime.now());
//        post2.setProducingChannelId("6134bcec27411d5faf124a1c");
//        post2.setRating(new PostRating(2d));
//
//        Post post3 = new Post();
//        post3.setCode("3");
//        post3.getPublishedTimeByType().put(PublicationType.INSTAGRAM_STORY, LocalDateTime.now());
//        post3.setProducingChannelId("6134bcec27411d5faf124a1c");
//        post3.setRating(new PostRating(3d));
//
//        postService.save(post1);
//        postService.save(post2);
//        postService.save(post3);

//        ProducingChannel producingChannel = producingChannelService.findById("6134bcec27411d5faf124a1c").get();

        System.out.println("Ищу пост");
        Post post = postService.findMostRatedPost("6134bcec27411d5faf124a1c").get();

        System.out.println(post);

//        System.out.println("Нашел " + list.size() + " шт.");
//        list.forEach(System.out::println);
    }
}
