package com.panyukovnn.common.repository;

import com.panyukovnn.common.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Customers repository
 */
@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByLogin(String login);
}
