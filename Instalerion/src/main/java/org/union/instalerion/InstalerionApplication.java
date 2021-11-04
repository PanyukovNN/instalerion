package org.union.instalerion;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.responses.media.MediaInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.union.common.model.Customer;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;
import org.union.common.repository.CustomerRepository;
import org.union.common.service.CustomerService;
import org.union.common.service.InstaService;
import org.union.common.service.ProducingChannelService;

import java.util.Optional;

@EnableScheduling
@EnableMongoRepositories(basePackages = "org.union.common.repository")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"org.union.instalerion", "org.union.common"})
public class InstalerionApplication implements CommandLineRunner {

    @Autowired
    InstaService instaService;

    @Autowired
    ProducingChannelService producingChannelService;

    @Autowired
    CustomerRepository customerRepository;

    public static void main(String[] args) {
        SpringApplication.run(InstalerionApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
//        ProducingChannel producingChannel = producingChannelService.findById("6134bcec27411d5faf124a1c").orElse(null);
//
//        InstaClient client = instaService.getClient(producingChannel);
//
//        MediaInfoResponse infoResponse = instaService.requestMediaInfo(client, 2663773837658154216L);
//
//        System.out.println(infoResponse);

        // TODO написать функцию публикации по mediaId и по code поста, тем самым будет проще тестировать и можно будет использовать ее в будущем



        Customer customer = new Customer();
        customer.setUsername("user");
        customer.setPassword("pass");
        customerRepository.save(customer);

        Customer dbCustomer = customerRepository.findById("6159fb02c5bec92ed057d045").get();
        dbCustomer.setPassword("lalallala");
        customerRepository.save(dbCustomer);
    }
}
