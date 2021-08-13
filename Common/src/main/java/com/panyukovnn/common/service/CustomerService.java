package com.panyukovnn.common.service;

import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    public final CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
