package com.panyukovnn.instalerion;

import com.panyukovnn.instalerion.module.Customer;
import com.panyukovnn.instalerion.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.List;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class InstalerionApplication implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    public static void main(String[] args) {
        SpringApplication.run(InstalerionApplication.class);

        // 1. Подключить монго
        // 2. Создать эндпоинт, на которой можно прислать канал публикации с каналами потребления
    }

    @Override
    public void run(String... args) throws Exception {
        Customer nick = new Customer();
        nick.setName("Nick");
        nick.setPassword("123");

        Customer mary = new Customer();
        mary.setName("Mary");
        mary.setPassword("456");

        customerRepository.save(nick);
        customerRepository.save(mary);

        List<Customer> allCustomers = customerRepository.findAll();

        allCustomers.forEach(System.out::println);
    }
}
