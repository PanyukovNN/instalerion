package org.union.common.repository;

import org.union.common.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Customers repository
 */
@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
}
