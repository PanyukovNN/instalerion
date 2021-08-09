package com.panyukovnn.instaloader.service;

import com.panyukovnn.common.model.ConsumeChannel;
import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.repository.CustomerRepository;
import com.panyukovnn.common.service.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Customer service
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final EncryptionUtil encryptionUtil;
    private final CustomerRepository customerRepository;

    /**
     * Save customer
     *
     * @param login login
     * @param password password
     * @param consumeChannels consume channels list
     */
    public void save(String login, String password, List<ConsumeChannel> consumeChannels) {
        Customer customer = new Customer();

        customer.setLogin(login);
        customer.setPassword(encryptionUtil.getTextEncryptor().encrypt(password));
        customer.setConsumeChannels(consumeChannels);

        customerRepository.save(customer);
    }

    public void appendConsumeChannels(List<ConsumeChannel> consumeChannels) {

    }

    public void removeConsumeChannels() {

    }
}
