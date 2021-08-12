package com.panyukovnn.common.service;

import com.panyukovnn.common.exception.NotFoundException;
import com.panyukovnn.common.model.ConsumeChannel;
import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.panyukovnn.common.Constants.CUSTOMER_NOT_FOUND_ERROR_MSG;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final EncryptionUtil encryptionUtil;
    private final CustomerRepository customerRepository;

    public void save(String login, String password, List<ConsumeChannel> consumeChannels) {
        Customer customer = new Customer();
        customer.setLogin(login);
        customer.setPassword(encryptionUtil.getTextEncryptor().encrypt(password));
        customer.setConsumeChannels(consumeChannels);

        customerRepository.save(customer);
    }

    public void addConsumeChannels(String customerId, List<ConsumeChannel> consumeChannels) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND_ERROR_MSG));

        if (customer.getConsumeChannels() == null) {
            customer.setConsumeChannels(new ArrayList<>());
        }

        for (ConsumeChannel consumeChannel : consumeChannels) {
            boolean matchByName = customer.getConsumeChannels().stream()
                    .anyMatch(cc -> cc.getName().equals(consumeChannel.getName()));

            if (!matchByName) {
                customer.getConsumeChannels().add(consumeChannel);
            }
        }

        customerRepository.save(customer);
    }
}
