package com.panyukovnn.common.repository;

import com.panyukovnn.common.model.ConsumingChannel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Consuming channels repository
 */
@Repository
public interface ConsumingChannelRepository extends MongoRepository<ConsumingChannel, String> {

    Optional<ConsumingChannel> findByName(String name);
}
