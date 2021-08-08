package com.panyukovnn.instalerion;

import com.panyukovnn.instalerion.module.Customer;
import com.panyukovnn.instalerion.repository.CustomerRepository;
import com.panyukovnn.instaloader.service.LoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@ComponentScan({"com.panyukovnn.instaloader", "com.panyukovnn.instalerion"})
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class InstalerionApplication implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoaderService loaderService;

    public static void main(String[] args) {
        SpringApplication.run(InstalerionApplication.class);

        // 1. Подключить монго
        // 2. Создать эндпоинт, на которой можно прислать канал публикации с каналами потребления
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
