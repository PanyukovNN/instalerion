package com.panyukovnn.common.service;

import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    public final CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> findById(String customerId) {
        return customerRepository.findById(customerId);
    }

    public void remove(Customer customer) {
        customerRepository.delete(customer);
    }
}
