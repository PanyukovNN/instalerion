package com.panyukovnn.instalerion;

import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.model.VideoPost;
import com.panyukovnn.common.model.request.UploadVideoRequest;
import com.panyukovnn.common.repository.CustomerRepository;
import com.panyukovnn.instalerion.service.kafka.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = {CustomerRepository.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"com.panyukovnn.instalerion", "com.panyukovnn.common"})
public class InstalerionApplication implements CommandLineRunner {

    @Autowired
    private KafkaSender kafkaSender;

    @Autowired
    private CustomerRepository customerRepository;

    public static void main(String[] args) {
        SpringApplication.run(InstalerionApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        String customerId = "6110322f8f21ee113e916f85";
//        kafkaSender.loaderCustomerIdSend("6110322f8f21ee113e916f85");

        Customer customer = customerRepository.findById(customerId).get();

        VideoPost videoPost = customer.getConsumeChannels().get(0).getVideoPosts().get(0);

        UploadVideoRequest request = new UploadVideoRequest(customerId, videoPost);

        kafkaSender.publisherUploadVideoSend(request);
    }
}
