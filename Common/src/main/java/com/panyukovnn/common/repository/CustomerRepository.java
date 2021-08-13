package com.panyukovnn.common.repository;

import com.panyukovnn.common.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Customers repository
 */
@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
}
