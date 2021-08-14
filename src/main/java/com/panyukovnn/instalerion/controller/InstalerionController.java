package com.panyukovnn.instalerion.controller;

import com.panyukovnn.common.exception.NotFoundException;
import com.panyukovnn.common.model.ConsumingChannel;
import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.model.ProducingChannel;
import com.panyukovnn.common.model.post.Post;
import com.panyukovnn.common.service.ConsumingChannelService;
import com.panyukovnn.common.service.CustomerService;
import com.panyukovnn.common.service.PostService;
import com.panyukovnn.common.service.ProducingChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.panyukovnn.common.Constants.CUSTOMER_NOT_FOUND_ERROR_MSG;

@RestController
@RequiredArgsConstructor
public class InstalerionController {

    private final PostService postService;
    private final CustomerService customerService;
    private final ProducingChannelService producingChannelService;
    private final ConsumingChannelService consumingChannelService;

    /*

    Посмотреть все посты
    Добавить/Удалить каналы потребления
    Удалить канал публикации

     */

    @GetMapping("/user/all")
    public List<Customer> getAllUsers() {
        return customerService.findAll();
    }

    @GetMapping("/producing-channel/all")
    public List<ProducingChannel> getAllProducingChannel() {
        return producingChannelService.findAll();
    }

    @GetMapping("/consuming-channel/all")
    public List<ConsumingChannel> getAllConsumingChannel() {
        return consumingChannelService.findAll();
    }

    @GetMapping("/post/all")
    public List<Post> getAllPosts() {
        return postService.findAll();
    }

    @PostMapping("/user/create")
    public String postCreateUser(@RequestParam String username,
                                 @RequestParam String password) {
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(password);

        customerService.save(customer);

        return String.format("User %s successfully saved.", username);
    }

    @PostMapping("/producing-channel/create")
    public String postCreateUser(@RequestParam String login,
                                 @RequestParam String password,
                                 @RequestParam List<String> consumingChannelNames,
                                 @RequestParam int postingPeriod,
                                 @RequestParam String customerId) {
        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new NotFoundException(String.format(CUSTOMER_NOT_FOUND_ERROR_MSG, customerId)));

        List<ConsumingChannel> consumingChannels = consumingChannelNames.stream()
                .map(ConsumingChannel::new)
                .collect(Collectors.toList());

        producingChannelService.create(login, password, consumingChannels, postingPeriod, customer);
        customerService.save(customer);

        return String.format("Producing channel %s successfully created.", login);
    }

    @GetMapping("/")
    public String getIndex() {
        return "index";
    }
}
