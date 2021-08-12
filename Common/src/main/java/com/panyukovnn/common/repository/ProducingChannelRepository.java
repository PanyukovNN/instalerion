package com.panyukovnn.common.repository;

import com.panyukovnn.common.model.ProducingChannel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Producing channels repository
 */
@Repository
public interface ProducingChannelRepository extends MongoRepository<ProducingChannel, String> {

    Optional<ProducingChannel> findByLogin(String login);
}
