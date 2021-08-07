package com.panyukovnn.instalerion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InstalerionApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstalerionApplication.class);

        // 1. Подключить монго
        // 2. Создать эндпоинт, на которой можно прислать канал публикации с каналами потребления
    }
}
