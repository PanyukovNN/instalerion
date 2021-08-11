package com.panyukovnn.common.repository;

import com.panyukovnn.common.model.Customer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Customers repository
 */
@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByLogin(String login);

    @Query("{ 'id' : ?0 }")
    Optional<Customer> findById(String id);
}
