package com.panyukovnn.instalerion.repository;

import com.panyukovnn.instalerion.module.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {

}
