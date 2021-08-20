package org.union.common.repository;

import org.union.common.model.Customer;
import org.union.common.model.ProducingChannel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Producing channels repository
 */
@Repository
public interface ProducingChannelRepository extends MongoRepository<ProducingChannel, String> {

    Optional<ProducingChannel> findByLogin(String login);

    List<ProducingChannel> findByCustomer(Customer customer);
}
