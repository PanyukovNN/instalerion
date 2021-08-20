package org.union.common.repository;

import org.union.common.model.ConsumingChannel;
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
