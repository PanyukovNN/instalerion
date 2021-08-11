package com.panyukovnn.common.service;

import com.panyukovnn.common.model.ConsumeChannel;
import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final EncryptionUtil encryptionUtil;
    private final CustomerRepository customerRepository;

    public void add(String login, String password, List<ConsumeChannel> consumeChannels) {
        Customer customer = new Customer();
        customer.setLogin(login);
        customer.setPassword(encryptionUtil.getTextEncryptor().encrypt(password));
        customer.setConsumeChannels(consumeChannels);

        customerRepository.save(customer);
    }
}
